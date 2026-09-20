import {
  constantRoutes
} from '@/router'
import Layout from '@/layout/index'
import useUserStore from '@/store/modules/user'

// 菜单地址即页面文件地址：/admin/tag/index → src/views/admin/tag/index.vue。
// 新增菜单时只需按该规则创建 Vue 文件，无须再维护前端路由别名或注册表。
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
  // 菜单来自后端，但只能解析构建时已登记的本地 Vue 文件；拒绝路径遍历、查询串和任意模块路径。
  if (!normalizedPath || !/^[A-Za-z0-9_/-]+$/.test(normalizedPath) || normalizedPath.includes('..')) return []
  const normalized = `/${normalizedPath}`
  return [`/src/views${normalized}.vue`]
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
      ...(redirect ? {
        redirect
      } : {
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

// 左侧导航只根据菜单管理接口返回的结构渲染；按钮权限不作为菜单显示。
function menuSidebarRoutes(items = []) {
  return (Array.isArray(items) ? items : [])
    .filter(item => String(item.type) !== '1')
    .sort((a, b) => Number(a.sort || 0) - Number(b.sort || 0))
    .map(item => {
      const id = menuId(item)
      const children = menuSidebarRoutes(item.children || [])
      return {
        path: item.path || `/menu-group/${id}`,
        name: `PoriaMenu${id}`,
        alwaysShow: children.length > 0,
        meta: {
          title: item.name || item.label || '未命名菜单',
          icon: item.icon || 'menu'
        },
        ...(children.length ? {
          children
        } : {})
      }
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
      this.addRoutes = routes;
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
      const sidebarRoutes = constantRoutes.concat(menuSidebarRoutes(menus))
      this.setRoutes(routes);
      this.setSidebarRouters(sidebarRoutes);
      this.setDefaultRoutes(routes);
      this.setTopbarRoutes(routes)
      return Promise.resolve(routes)
    }
  }
})

export default usePermissionStore
