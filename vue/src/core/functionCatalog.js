import { consoleMenus, isExternal, pageFor } from './menu'

export function buildFunctionCatalog(menus, { includeProfile = false } = {}) {
  const entries = []
  const paths = new Set()
  function append(nodes, ancestors = []) {
    for (const menu of nodes) {
      if (String(menu.type) === '1' || String(menu.delFlag ?? '0') === '1') continue
      const title = menu.title || menu.name || '未命名功能'
      const external = isExternal(menu.path)
      const path = menu.path
      const available = path && (external || path === '/profile/index' || pageFor(path, title))
      if (available && !['/', '/index', '/functions/index'].includes(path) && !paths.has(path)) {
        paths.add(path)
        entries.push({
          title, path, external, icon: menu.icon || 'grid',
          category: ancestors.length ? ancestors.join(' / ') : '其他功能'
        })
      }
      append(menu.children || [], [...ancestors, title])
    }
  }
  append(consoleMenus(menus))
  if (includeProfile && !paths.has('/profile/index')) {
    entries.push({ title: '个人中心', path: '/profile/index', external: false, icon: 'user', category: '我的账号' })
  }
  return entries
}

export function filterFunctions(entries, { keyword = '', category = '', pinnedOnly = false, pinnedPaths = [] } = {}) {
  const terms = keyword.trim().toLocaleLowerCase().split(/\s+/).filter(Boolean)
  const pins = new Set(pinnedPaths)
  return entries.filter(entry => (!category || entry.category === category)
    && (!pinnedOnly || pins.has(entry.path))
    && terms.every(term => `${entry.title} ${entry.category} ${entry.path}`.toLocaleLowerCase().includes(term)))
}

export function readPinnedFunctions(storage, key) {
  if (!key) return []
  try {
    const saved = JSON.parse(storage.getItem(key) || '[]')
    return Array.isArray(saved) ? [...new Set(saved.filter(path => typeof path === 'string'))].slice(0, 100) : []
  } catch { return [] }
}
