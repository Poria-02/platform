import { http } from './http'
import { readSession } from './token'

const cache = new Map()
const cacheLifetime = 5 * 60 * 1000
let sessionId

export function invalidateDictionaries() {
  cache.clear()
}

export async function mutateDictionary(request) {
  const result = await request
  if (result !== false) invalidateDictionaries()
  return result
}

export async function fetchDictionary(type) {
  const currentId = readSession()?.idleId
  if (currentId !== sessionId) {
    invalidateDictionaries()
    sessionId = currentId
  }
  let entry = cache.get(type)
  if (entry?.items && entry.expiresAt > Date.now()) return structuredClone(entry.items)
  if (!entry?.request) {
    entry = {}
    cache.set(type, entry)
    entry.request = http.get(`/upms/dict/type/${encodeURIComponent(type)}`, { silent: true }).then(items => {
      if (!Array.isArray(items)) throw new Error('字典数据格式错误')
      entry.items = items
      entry.expiresAt = Date.now() + cacheLifetime
      return items
    }).catch(error => {
      if (cache.get(type) === entry) cache.delete(type)
      throw error
    }).finally(() => { entry.request = null })
  }
  return structuredClone(await entry.request)
}
