import { createRouter, createWebHistory } from 'vue-router'
import { useSession } from './session'
import { firstPagePath, flattenMenus, isPageMenu, pageFiles, pageFor } from './menu'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('@/views/login/index.vue'), meta: { public: true } },
    {
      path: '/', name: 'console', component: () => import('@/layout/ConsoleLayout.vue'), redirect: '/index',
      children: [
        { path: 'index', name: 'home', component: () => import('@/views/index/index.vue'), meta: { title: '工作台' } },
        { path: 'functions/index', name: 'functions', component: () => import('@/views/functions/index.vue'), meta: { title: '全部功能' } },
        { path: 'profile/index', name: 'profile', component: () => import('@/views/profile/index.vue'), meta: { title: '个人中心' } }
      ]
    },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/not-found/index.vue') }
  ]
})

const installed = new Set()

export function installMenuRoutes(menus) {
  for (const menu of flattenMenus(menus)) {
    if (!isPageMenu(menu) || ['/index', '/functions/index', '/profile/index'].includes(menu.path) || installed.has(menu.path)) continue
    const component = pageFor(menu.path, menu.title)
    const redirect = component ? '' : firstPagePath(menu.children)
    const route = {
      path: menu.path.slice(1),
      name: `menu:${menu.path}`,
      meta: { title: menu.title, icon: menu.icon, menuPath: menu.path, expectedFiles: pageFiles(menu.path, menu.title) },
      ...(redirect ? { redirect } : { component: component || (() => import('@/views/not-found/index.vue')) })
    }
    router.addRoute('console', route)
    installed.add(menu.path)
  }
}

export function clearMenuRoutes() {
  for (const path of installed) router.removeRoute(`menu:${path}`)
  installed.clear()
}

router.beforeEach(async to => {
  const session = useSession()
  if (to.meta.public) return session.authenticated ? '/index' : true
  if (!session.authenticated) return { path: '/login', query: { redirect: to.fullPath } }
  if (to.name === 'home') return true
  if (!session.menusReady) {
    try {
      installMenuRoutes(await session.ensureMenus())
      return { path: to.fullPath, replace: true }
    } catch {
      if (!session.authenticated) return { path: '/login', query: { redirect: to.fullPath } }
      return to.path === '/index' ? true : '/index'
    }
  }
  return true
})

export default router
