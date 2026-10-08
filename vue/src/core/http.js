import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearSession, readSession } from './token'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_PREFIX || '/api',
  timeout: 15000
})

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
        if (!config.silent) ElMessage.error(refreshError.message || '登录续期失败')
        return Promise.reject(refreshError)
      }
    }
    clearSession()
    const [{ useSession }, { default: router, clearMenuRoutes }] = await Promise.all([import('./session'), import('./router')])
    useSession().$reset()
    clearMenuRoutes()
    if (router.currentRoute.value.path !== '/login') {
      router.replace({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
    }
  }
  if (!config.silent && !(unauthorized && !hadSession)) ElMessage.error(body?.msg || error.message || '网络连接失败')
  return Promise.reject(error)
}

http.interceptors.response.use(response => {
  try { return readResponse(response) }
  catch (error) { return handleError(error) }
}, handleError)
