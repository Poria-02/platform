const storageKey = 'poria.console.session'
const menuStorageKey = 'poria.console.menus'
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
    departmentId: user ? user.orgId ?? null : previous.departmentId ?? null,
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

function validMenuTree(menus) {
  return Array.isArray(menus) && menus.every(menu => menu && typeof menu === 'object' && validMenuTree(menu.children))
}

export function readMenuCache(session) {
  try {
    if (!session?.idleId || sessionIsIdle(session)) return null
    const cached = JSON.parse(sessionStorage.getItem(menuStorageKey) || 'null')
    if (cached?.version !== 1 || cached.idleId !== session.idleId || !validMenuTree(cached.menus)) return null
    return cached.menus
  } catch {
    return null
  }
}

export function saveMenuCache(sessionId, menus) {
  try {
    sessionStorage.setItem(menuStorageKey, JSON.stringify({ version: 1, idleId: sessionId, menus }))
  } catch {
    // 持久化失败时保留内存菜单，清除可能过期的旧缓存。
    clearMenuCache()
  }
}

export function clearMenuCache() {
  try { sessionStorage.removeItem(menuStorageKey) } catch { /* 缓存不可用时仍可使用内存菜单。 */ }
}

export function initializePageSession(navigationType = globalThis.performance?.getEntriesByType?.('navigation')?.[0]?.type) {
  // 仅普通刷新可以恢复登录；重新打开或恢复关闭的页面需重新登录。
  if (navigationType !== 'reload' || !readSession()) clearSession(undefined, { removeActivity: false })
}

export function clearSession(sessionId, { removeActivity = true } = {}) {
  let idleId = sessionId
  try { idleId ??= JSON.parse(sessionStorage.getItem(storageKey) || 'null')?.idleId } catch { /* 无效会话直接清除。 */ }
  sessionStorage.removeItem(storageKey)
  clearMenuCache()
  if (idleId && removeActivity) localStorage.removeItem(activityKey(idleId))
}
