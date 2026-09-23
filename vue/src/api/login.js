import request from '@/utils/request'
import { encryptAesCfb } from '@/utils/aes'
import { authUrl, upmsUrl } from '@/utils/api-url'

const authEncodeKey = import.meta.env.VITE_AUTH_ENCODE_KEY || 'shanxincdJinkang'

// Poria Auth 的 plat 登录会校验 OAuth 客户端并解密用户名、密码。
export function login(data = {}) {
  const clientId = import.meta.env.VITE_AUTH_CLIENT_ID
  const clientSecret = import.meta.env.VITE_AUTH_CLIENT_SECRET || clientId
  if (!clientId) {
    return Promise.reject(new Error('缺少 OAuth 客户端配置：请设置 VITE_AUTH_CLIENT_ID。'))
  }
  const username = data.username?.trim() || ''
  return Promise.all([
    encryptAesCfb(username, authEncodeKey),
    encryptAesCfb(data.password || '', authEncodeKey)
  ]).then(([encryptedUsername, encryptedPassword]) => request({
    url: authUrl('/plat/login'),
    method: 'post',
    headers: {
      isToken: false,
      repeatSubmit: false,
      Authorization: `Basic ${window.btoa(`${clientId}:${clientSecret}`)}`
    },
    skipAuthExpiredHandling: true,
    params: { grant_type: 'plat' },
    data: {
      username: encryptedUsername,
      password: encryptedPassword,
      loginType: data.loginType || 'ADMIN_PWD',
      userType: data.userType ?? import.meta.env.VITE_AUTH_USER_TYPE ?? '',
      verifyCode: data.verifyCode ?? data.code ?? '',
      randomStr: data.randomStr ?? data.uuid ?? '',
      appId: data.appId ?? '',
      scope: data.scope || import.meta.env.VITE_AUTH_SCOPE || 'server',
      additionalParameters: data.additionalParameters ?? {}
    }
  }))
}

// UPMS 菜单树同时作为若依动态路由数据来源。
export function getInfo() {
  const platform = import.meta.env.VITE_MENU_PLATFORM || import.meta.env.VITE_AUTH_USER_TYPE
  return request({
    url: upmsUrl('/menu'),
    method: 'get',
    params: platform ? { platform } : undefined
  })
}

export function logout() {
  return request({
    url: authUrl('/token/logout'),
    method: 'delete'
  })
}

// Poria Auth 返回图片二进制，uuid/randomStr 用于和登录请求关联。
export function getCodeImg(randomStr) {
  return request({
    url: `/code?randomStr=${encodeURIComponent(randomStr)}`,
    headers: { isToken: false },
    method: 'get',
    responseType: 'blob',
    timeout: 20000
  })
}
