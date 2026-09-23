import { constantRoutes } from '@/router'
import Layout from '@/layout/index'
import useUserStore from '@/store/modules/user'

const pageModules = import.meta.glob('/src/views/**/*.vue')

function menuId(menu) {
  return menu.id ?? menu.menuId
}

function menuEntries(items = []) {
  return (Array.isArray(items) ? items : []).flatMap(item => [item, ...menuEntries(item.children || [])])
}

function isExternal(path) {
  return /^https?:\/\//i.test(path || '')
}

function isMenuPage(menu) {
  return String(menu.type) !== '1' && Boolean(menu.path) && !isExternal(menu.path)
}

function pageFileCandidates(path) {
  const normalizedPath = String(path).replace(/^\/+|\/+$/g, '')
  if (!normalizedPath || !/^[A-Za-z0-9_/-]+$/.test(normalizedPath) || normalizedPath.includes('..')) return []
  const normalized = `/${normalizedPath}`
  return [`/src/views${normalized}.vue`, `/src/views${normalized}/index.vue`]
}

function componentFor(path) {
  return pageFileCandidates(path).map(file => pageModules[file]).find(Boolean) ||
    (() => import('@/views/error/404.vue'))
}

function descendantPagePath(menu) {
  for (const child of menu.children || []) {
    if (isMenuPage(child)) return child.path
    const found = descendantPagePath(child)
    if (found) return found
  }
}

function menuPageRoutes(menus) {
  const seenPaths = new Set()
  return menuEntries(menus).flatMap(menu => {
    if (!isMenuPage(menu) || seenPaths.has(menu.path)) return []
    seenPaths.add(menu.path)
    const id = menuId(menu)
    const redirect = descendantPagePath(menu)
    const meta = {
      title: menu.name || menu.label || '未命名菜单',
      icon: menu.icon || 'menu',
      sourcePath: menu.path
    }
    return [{
      path: menu.path,
      component: Layout,
      name: `PoriaMenuPage${id}`,
      ...(redirect ? { redirect } : {
        children: [{
          path: '',
          component: componentFor(menu.path),
          name: `PoriaMenuView${id}`,
          meta
        }]
      })
    }]
  })
}

const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  }),
  actions: {
    setRoutes(routes) {
      this.addRoutes = routes
      this.routes = constantRoutes.concat(routes)
    },
    setDefaultRoutes(routes) {
      this.defaultRoutes = constantRoutes.concat(routes)
    },
    setTopbarRoutes(routes) {
      this.topbarRouters = routes
    },
    setSidebarRouters(routes) {
      this.sidebarRouters = routes
    },
    generateRoutes() {
      const menus = useUserStore().menus
      const routes = menuPageRoutes(menus)
      this.setRoutes(routes)
      this.setSidebarRouters(menus)
      this.setDefaultRoutes(routes)
      this.setTopbarRoutes(routes)
      return Promise.resolve(routes)
    }
  }
})

export default usePermissionStore
