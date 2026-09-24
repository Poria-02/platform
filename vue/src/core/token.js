const storageKey = 'poria.console.session'
export const activityKey = id => `poria.console.activity.${id}`

export function readSession() {
  try {
    const value = JSON.parse(sessionStorage.getItem(storageKey) || 'null')
    if (!value?.accessToken || (value.expiresAt && Date.now() >= value.expiresAt)) return null
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
