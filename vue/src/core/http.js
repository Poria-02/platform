import axios from 'axios'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { readSession } from './token'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_PREFIX || '/api',
  timeout: 15000
})

const notifiedErrors = new WeakSet()

export function requestErrorMessage(error, fallback = '网络连接失败，请检查网络后重试') {
  const status = error.response?.status
  const body = error.response?.data
  const config = error.config || error.response?.config || {}
  const message = body?.msg || error.message || fallback
  if (error.sessionExpired) return '登录已失效，请重新登录'
  if (status === 403 || message === '服务器内部错误') return message
  if (!config.skipAuthRedirect && (status === 401 || (status === 424 && body?.msg === '无效的权限'))) {
    return '登录已失效，请重新登录'
  }
  if (status === 504 || ['ECONNABORTED', 'ETIMEDOUT'].includes(error.code) || /^timeout of \d+ms exceeded$|\b(?:connect|read) timed out\b/i.test(message)) {
    return '请求超时，请稍后重试'
  }
  if ([502, 503].includes(status) || /\b(?:Connection refused|ECONNREFUSED)\b|No servers available for service|Unable to find instance for/i.test(message)) {
    return '当前功能暂不可用，请稍后重试'
  }
  if (!error.response && (error.code === 'ERR_NETWORK' || message === 'Network Error')) {
    return '网络连接失败，请检查网络后重试'
  }
  return message
}

export function showRequestError(error, fallback = '请求失败') {
  if (axios.isCancel(error) || notifiedErrors.has(error)) return
  notifiedErrors.add(error)
  ElMessage.error({ message: requestErrorMessage(error, fallback), grouping: true })
}

http.interceptors.request.use(async config => {
  if (config.skipToken) return config
  const current = readSession()
  let token = current?.accessToken
  if (current) {
    config.authSessionId = current.idleId
    const { useSession } = await import('./session')
    try { token = await useSession().ensureAccessToken() }
    catch (error) {
      // 临时网络故障时，仍有效的旧令牌可以继续使用。
      if (error.sessionExpired || !current.expiresAt || Date.now() >= current.expiresAt) {
        error.config = config
        throw error
      }
    }
    if (useSession().current?.idleId !== current.idleId) {
      throw Object.assign(new Error('登录会话已改变'), { config, sessionExpired: true })
    }
  }
  if (token) config.headers.Authorization = `Bearer ${token}`
  config.authToken = token
  return config
})

function readResponse(response) {
  if (response.config.responseType === 'blob') return response.data
  const body = response.data
  if (body && typeof body === 'object' && typeof body.code === 'number') {
    if (body.code !== 0) {
      throw Object.assign(new Error(body.msg || '请求失败'), { code: body.code, response })
    }
    return body.data
  }
  if (response.config.oauthResponse && body?.access_token) return body
  throw Object.assign(new Error('服务返回了未知的数据格式'), { response })
}

async function handleError(error) {
  if (axios.isCancel(error)) return Promise.reject(error)
  error.message = requestErrorMessage(error)
  const config = error.config || error.response?.config || {}
  const body = error.response?.data
  const hadSession = Boolean(readSession())
  const unauthorized = error.response?.status === 401 ||
    (error.response?.status === 424 && body?.msg === '无效的权限')
  if (unauthorized && !config.skipAuthRedirect) {
    const current = readSession()
    if (config.authSessionId && config.authSessionId !== current?.idleId) return Promise.reject(error)
    if (!config.skipToken && !config.authRetried && current?.refreshToken) {
      const { useSession } = await import('./session')
      try {
        const token = await useSession().ensureAccessToken({ force: true, failedToken: config.authToken })
        if (!token || useSession().current?.idleId !== current.idleId) {
          throw Object.assign(new Error('登录会话已改变'), { sessionExpired: true })
        }
        return http({ ...config, authRetried: true })
      } catch (refreshError) {
        refreshError.message = requestErrorMessage(refreshError, '登录续期失败')
        if (!config.silent) showRequestError(refreshError)
        return Promise.reject(refreshError)
      }
    }
    const { useSession } = await import('./session')
    const session = useSession()
    if (config.authSessionId && config.authSessionId !== session.current?.idleId) return Promise.reject(error)
    session.clearLocal()
    await session.redirectToLogin()
  }
  if (!config.silent && !(unauthorized && !hadSession)) showRequestError(error)
  return Promise.reject(error)
}

http.interceptors.response.use(response => {
  try { return readResponse(response) }
  catch (error) { return handleError(error) }
}, handleError)
