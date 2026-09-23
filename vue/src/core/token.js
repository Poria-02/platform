const storageKey = 'poria.console.session'

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
  sessionStorage.removeItem(storageKey)
}
