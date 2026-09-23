import { fetchMyMenus } from '@/views/admin/menu/api'

const pages = import.meta.glob('/src/views/**/index.vue')
const pageApis = import.meta.glob('/src/views/**/api.js')

export function isExternal(path) {
  return /^https?:\/\//i.test(path || '')
}

export function resolveMenuPath(rawPath, parentPath = '') {
  const path = String(rawPath ?? '').trim()
  if (!path) return ''
  if (isExternal(path)) return path
  if (path === '/') return '/'
  if (!/^\/?[A-Za-z0-9_-]+(?:\/[A-Za-z0-9_-]+)*\/?$/.test(path)) return ''
  const base = parentPath.startsWith('/') && parentPath !== '/' ? parentPath : ''
  return (path.startsWith('/') ? path : `${base}/${path}`).replace(/\/$/, '')
}

export function normalizeMenus(items, parentPath = '') {
  return (Array.isArray(items) ? items : [])
    .filter(item => String(item.delFlag ?? '0') !== '1')
    .map(item => {
      const path = resolveMenuPath(item.path, parentPath)
      const base = path && !isExternal(path) ? path : parentPath
      return {
        ...item,
        path,
        id: item.id ?? item.menuId,
        title: item.name || item.label || '未命名菜单',
        children: normalizeMenus(item.children, base)
      }
    })
    .sort((left, right) => Number(left.sort ?? 0) - Number(right.sort ?? 0))
}

export function flattenMenus(items) {
  return (items || []).flatMap(item => [item, ...flattenMenus(item.children)])
}

export function isPageMenu(item) {
  return String(item.type) !== '1' && item.path && item.path !== '/' && !isExternal(item.path)
}

export function pageFiles(path) {
  const directory = path.endsWith('/index') ? path.slice(0, -6) : path
  return {
    view: `src/views${directory}/index.vue`,
    api: `src/views${directory}/api.js`
  }
}

export function pageFor(path) {
  const { view, api } = pageFiles(path)
  return pages[`/${view}`] && pageApis[`/${api}`] ? pages[`/${view}`] : null
}

export function firstPagePath(items) {
  for (const item of items || []) {
    if (String(item.type) === '1') continue
    if (isPageMenu(item) && pageFor(item.path)) return item.path
    const nested = firstPagePath(item.children)
    if (nested) return nested
  }
  return ''
}

export async function fetchMenus() {
  return normalizeMenus(await fetchMyMenus())
}
