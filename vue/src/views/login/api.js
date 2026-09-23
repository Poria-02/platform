import { http } from '@/core/http'
import { encryptForAuth } from './crypto'

export async function login(credentials) {
  const clientId = import.meta.env.VITE_AUTH_CLIENT_ID
  const clientSecret = import.meta.env.VITE_AUTH_CLIENT_SECRET || clientId
  const encodeKey = import.meta.env.VITE_AUTH_ENCODE_KEY || 'shanxincdJinkang'
  if (!clientId) throw new Error('缺少认证客户端配置 VITE_AUTH_CLIENT_ID')

  const [username, password] = await Promise.all([
    encryptForAuth(credentials.username.trim(), encodeKey),
    encryptForAuth(credentials.password, encodeKey)
  ])
  return http.post('/auth/plat/login', {
    username,
    password,
    loginType: 'ADMIN_PWD',
    userType: import.meta.env.VITE_AUTH_USER_TYPE || '',
    verifyCode: credentials.verifyCode,
    randomStr: credentials.randomStr,
    appId: '',
    scope: import.meta.env.VITE_AUTH_SCOPE || 'server',
    additionalParameters: {}
  }, {
    params: { grant_type: 'plat' },
    headers: { Authorization: `Basic ${btoa(`${clientId}:${clientSecret}`)}` },
    skipToken: true,
    skipAuthRedirect: true,
    silent: true,
    oauthResponse: true
  })
}

export async function captcha(randomStr) {
  const blob = await http.get('/code', { params: { randomStr }, responseType: 'blob', headers: { Accept: 'text/plain' }, skipToken: true, skipAuthRedirect: true, silent: true })
  if (!blob.type.startsWith('image/')) throw new Error('验证码服务暂不可用')
  return blob
}

export function logout() {
  return http.delete('/auth/token/logout', { silent: true })
}
