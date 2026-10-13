import { nextTick, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const pageSizes = [10, 20, 50]
const scalar = value => Array.isArray(value) ? value[0] : value
const isEmpty = value => value === '' || value == null

function positiveInteger(value, fallback) {
  const number = Number(scalar(value))
  return Number.isSafeInteger(number) && number > 0 ? number : fallback
}

export function readListQuery(raw, defaults, pageDefaults, allowedValues = {}) {
  const filters = Object.fromEntries(Object.entries(defaults).map(([key, fallback]) => {
    const value = scalar(raw[key])
    const parsed = value == null ? fallback : String(value)
    return [key, allowedValues[key] && !allowedValues[key].includes(parsed) ? fallback : parsed]
  }))
  if (!pageDefaults) return { filters }
  const size = positiveInteger(raw.size, pageDefaults.size)
  return {
    filters,
    current: positiveInteger(raw.current, pageDefaults.current),
    size: pageSizes.includes(size) ? size : pageDefaults.size
  }
}

export function writeListQuery(raw, state, defaults, pageDefaults) {
  const next = { ...raw }
  for (const key of Object.keys(defaults)) {
    delete next[key]
    const value = state.filters[key]
    if (!isEmpty(value) && value !== defaults[key]) next[key] = String(value)
  }
  if (pageDefaults) {
    delete next.current
    delete next.size
    if (state.current !== pageDefaults.current) next.current = String(state.current)
    if (state.size !== pageDefaults.size) next.size = String(state.size)
  }
  return next
}

export function useListRouteState({ query = {}, page, reload, isLoading = () => false, allowedValues = {} }) {
  const route = useRoute()
  const router = useRouter()
  const path = route.path
  const defaults = { ...query }
  const pageDefaults = page ? { ...page } : undefined
  const read = raw => readListQuery(raw, defaults, pageDefaults, allowedValues)
  const pendingWrites = new Map()
  let disposed = false
  let reloadQueued = false
  let lastSignature

  function restore() {
    const state = read(route.query)
    Object.assign(query, state.filters)
    if (page) { page.current = state.current; page.size = state.size }
    lastSignature = JSON.stringify(state)
  }
  restore()

  async function syncListRoute() {
    if (disposed || route.path !== path) return null
    const state = read({ ...query, ...(page || {}) })
    const params = Object.fromEntries(Object.entries(state.filters).filter(([, value]) => !isEmpty(value)))
    if (page) { params.current = state.current; params.size = state.size }
    const next = writeListQuery(route.query, state, defaults, pageDefaults)
    const same = Object.keys(next).length === Object.keys(route.query).length
      && Object.keys(next).every(key => JSON.stringify(next[key]) === JSON.stringify(route.query[key]))
    const signature = JSON.stringify(state)
    if (same) { lastSignature = signature; return params }
    pendingWrites.set(signature, (pendingWrites.get(signature) || 0) + 1)
    try {
      await router.replace({ path, query: next, hash: route.hash })
      if (disposed || route.path !== path || JSON.stringify(read(route.query)) !== signature) return null
      lastSignature = signature
      return params
    } finally {
      await nextTick()
      const remaining = pendingWrites.get(signature) - 1
      if (remaining) pendingWrites.set(signature, remaining)
      else pendingWrites.delete(signature)
    }
  }

  watch(() => [route.path, JSON.stringify(read(route.query))], ([activePath, signature]) => {
    if (disposed || activePath !== path) return
    if (pendingWrites.has(signature)) { lastSignature = signature; return }
    if (signature === lastSignature) return
    restore()
    void reload()
  })

  function queueReload() {
    if (reloadQueued) return
    reloadQueued = true
    void nextTick(() => {
      reloadQueued = false
      if (!disposed && route.path === path) void reload()
    })
  }
  function changePage(value) {
    if (!page || isLoading() || route.path !== path) return
    const current = positiveInteger(value, 1)
    if (page.current === current) return
    page.current = current
    queueReload()
  }
  function changePageSize(value) {
    if (!page || isLoading() || route.path !== path) return
    const size = positiveInteger(value, pageDefaults.size)
    if (!pageSizes.includes(size) || page.size === size) return
    page.size = size
    page.current = 1
    queueReload()
  }

  onBeforeUnmount(() => { disposed = true })
  return { syncListRoute, changePage, changePageSize }
}
