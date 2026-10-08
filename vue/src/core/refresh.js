import { sessionIsIdle, tokenDetails } from './token.js'

export function refreshIsRejected(error) {
  const status = error.response?.status
  return [400, 401, 403].includes(status) || (status === 200 && error.code !== undefined)
}

// 同一会话的并发请求共用一次刷新；退出或重新登录后不再写入旧请求的结果。
export function createTokenRefresher({ getSession, setSession, requestRefresh, expireSession, revokeToken }) {
  let pending

  return async function ensureAccessToken({ force = false, failedToken } = {}) {
    const current = getSession()
    if (!current) return null
    const expiredError = () => Object.assign(new Error('登录已失效，请重新登录'), { sessionExpired: true })
    const expire = async () => {
      await expireSession(current.idleId)
      throw expiredError()
    }
    if (sessionIsIdle(current)) return expire()
    if (failedToken && failedToken !== current.accessToken) return current.accessToken
    const refreshAt = current.refreshAt ?? (current.expiresAt ? current.expiresAt - 60000 : null)
    if (!force && (!refreshAt || Date.now() < refreshAt)) return current.accessToken
    if (!current.refreshToken) {
      if (force || (current.expiresAt && Date.now() >= current.expiresAt)) return expire()
      return current.accessToken
    }
    if (current.refreshExpiresAt && Date.now() >= current.refreshExpiresAt) return expire()
    if (pending?.id === current.idleId) return pending.promise

    const operation = { id: current.idleId }
    operation.promise = (async () => {
      try {
        const response = await requestRefresh(current.refreshToken)
        let tokens
        try { tokens = tokenDetails(response, current) }
        catch (error) { error.refreshRejected = true; throw error }
        const latest = getSession()
        if (latest?.idleId !== current.idleId || sessionIsIdle(latest)) {
          await revokeToken(tokens.accessToken).catch(() => undefined)
          if (latest?.idleId === current.idleId) await expireSession(current.idleId)
          throw expiredError()
        }
        setSession({ ...latest, ...tokens })
        return tokens.accessToken
      } catch (error) {
        if (getSession()?.idleId === current.idleId && (error.refreshRejected || refreshIsRejected(error))) {
          await expireSession(current.idleId)
          error.sessionExpired = true
        }
        throw error
      } finally {
        if (pending === operation) pending = null
      }
    })()
    pending = operation
    return operation.promise
  }
}
