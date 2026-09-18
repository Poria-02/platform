import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token, expiresIn) {
  const options = {
    sameSite: 'strict',
    secure: window.location.protocol === 'https:'
  }
  if (Number.isFinite(expiresIn) && expiresIn > 0) {
    options.expires = new Date(Date.now() + expiresIn * 1000)
  }
  return Cookies.set(TokenKey, token, options)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
