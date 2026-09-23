import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearSession, readSession } from './token'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_PREFIX || '/api',
  timeout: 15000
})

http.interceptors.request.use(config => {
  const token = readSession()?.accessToken
  if (token && !config.skipToken) config.headers.Authorization = `Bearer ${token}`
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
  const unauthorized = error.response?.status === 401 ||
    (error.response?.status === 424 && body?.msg === '无效的权限')
  if (unauthorized && !config.skipAuthRedirect) {
    clearSession()
    const [{ useSession }, { default: router }] = await Promise.all([import('./session'), import('./router')])
    useSession().$reset()
    if (router.currentRoute.value.path !== '/login') {
      router.replace({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
    }
  }
  if (!config.silent) ElMessage.error(body?.msg || error.message || '网络连接失败')
  return Promise.reject(error)
}

http.interceptors.response.use(response => {
  try { return readResponse(response) }
  catch (error) { return handleError(error) }
}, handleError)
