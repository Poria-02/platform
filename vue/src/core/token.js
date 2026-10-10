const storageKey = 'poria.console.session'
export const idleTimeout = 30 * 60 * 1000
export const activityKey = id => `poria.console.activity.${id}`

export function sessionIsIdle(session, now = Date.now()) {
  const sharedActivity = session?.idleId ? Number(localStorage.getItem(activityKey(session.idleId))) || 0 : 0
  return now - Math.max(Number(session?.lastActivityAt) || 0, sharedActivity) >= idleTimeout
}

function tokenTime(value) {
  if (typeof value === 'number') return value < 100000000000 ? value * 1000 : value
  return Date.parse(value)
}

export function tokenDetails(data, previous = {}, now = Date.now()) {
  const access = data?.access_token ?? data?.accessToken ?? data?.token
  const accessToken = typeof access === 'string' ? access : access?.tokenValue
  if (!accessToken) throw new Error('认证服务未返回 access_token')
  const refresh = data?.refresh_token ?? data?.refreshToken
  const refreshToken = (typeof refresh === 'string' ? refresh : refresh?.tokenValue) || previous.refreshToken || null
  const seconds = Number(data?.expires_in ?? data?.expiresIn)
  const nestedExpiry = tokenTime(access?.expiresAt)
  const expiresAt = Number.isFinite(nestedExpiry) ? nestedExpiry : seconds > 0 ? now + seconds * 1000 : null
  const refreshExpiry = tokenTime(refresh?.expiresAt)
  const user = data?.user_info ?? data?.additionalParameters?.user_info
  const authorities = user?.authorities
  return {
    accessToken,
    refreshToken,
    permissions: Array.isArray(authorities)
      ? authorities.map(item => typeof item === 'string' ? item : item?.authority).filter(Boolean)
      : previous.permissions || [],
    expiresAt,
    refreshAt: expiresAt ? expiresAt - Math.min(60000, Math.max(0, (expiresAt - now) / 10)) : null,
    refreshExpiresAt: Number.isFinite(refreshExpiry) ? refreshExpiry
      : refreshToken === previous.refreshToken ? previous.refreshExpiresAt || null : null
  }
}

export function readSession() {
  try {
    const value = JSON.parse(sessionStorage.getItem(storageKey) || 'null')
    if (!value?.accessToken || (value.expiresAt && Date.now() >= value.expiresAt && !value.refreshToken)) return null
    return value
  } catch {
    return null
  }
}

export function saveSession(session) {
  sessionStorage.setItem(storageKey, JSON.stringify(session))
}

export function clearSession() {
  let idleId
  try { idleId = JSON.parse(sessionStorage.getItem(storageKey) || 'null')?.idleId } catch { /* 无效会话直接清除。 */ }
  sessionStorage.removeItem(storageKey)
  if (idleId) localStorage.removeItem(activityKey(idleId))
}
