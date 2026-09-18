import request from '@/utils/request'
import { encryptAesCfb } from '@/utils/aes'

// Must match Auth's ENCODE_KEY. The default preserves the current backend setup;
// deployments overriding ENCODE_KEY must set VITE_AUTH_ENCODE_KEY at build time.
const authEncodeKey = import.meta.env.VITE_AUTH_ENCODE_KEY || 'shanxincdJinkang'

// 登录方法：按 Poria Auth 的 PlatLoginModel 完整契约换取不透明访问令牌。
// 用户名、密码由认证服务解密；其余字段按原值传递给对应登录方式。
export function login(data = {}) {
  const clientId = import.meta.env.VITE_AUTH_CLIENT_ID
  // Legacy initialized clients use clientId as their secret. A separately supplied
  // secret still takes precedence for deployments that do not use that convention.
  const clientSecret = import.meta.env.VITE_AUTH_CLIENT_SECRET || clientId
  if (!clientId) {
    return Promise.reject(new Error('缺少 OAuth 客户端配置：请设置 VITE_AUTH_CLIENT_ID。'))
  }
  const scope = import.meta.env.VITE_AUTH_SCOPE || 'server'
  const defaultUserType = import.meta.env.VITE_AUTH_USER_TYPE || ''
  const username = data.username?.trim() || ''

  // PlatAuthenticationConverter reads the login model from JSON, including scope.
  // It decrypts password first; AdminPwdLoginService then decrypts username.
  return Promise.all([
    encryptAesCfb(username, authEncodeKey),
    encryptAesCfb(data.password || '', authEncodeKey)
  ]).then(([encryptedUsername, encryptedPassword]) => request({
      url: '/auth/plat/login',
      method: 'post',
      headers: {
        isToken: false,
        repeatSubmit: false,
        Authorization: `Basic ${window.btoa(`${clientId}:${clientSecret}`)}`
      },
      params: { grant_type: 'plat' },
      data: {
        username: encryptedUsername,
        password: encryptedPassword,
        loginType: data.loginType || 'ADMIN_PWD',
        userType: data.userType ?? defaultUserType,
        verifyCode: data.verifyCode ?? '',
        randomStr: data.randomStr ?? '',
        appId: data.appId ?? '',
        scope: data.scope || scope,
        additionalParameters: data.additionalParameters ?? {}
      }
    })
  )
}

// 锁屏不调用不存在的服务端端点；重新走当前账号的正式登录校验。
export function unlockScreen(username, password) {
  return login({ username, password })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/upms/menu',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/auth/token/logout',
    method: 'delete'
  })
}

