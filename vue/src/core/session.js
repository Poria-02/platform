import { defineStore } from 'pinia'
import { watch } from 'vue'
import { login, logout, refreshTokens } from '@/views/login/api'
import { fetchMenus } from './menu'
import { clearSession, readSession, saveSession, tokenDetails } from './token'
import { createTokenRefresher } from './refresh'

const refreshers = new WeakMap()

export const useSession = defineStore('session', {
  state: () => ({
    current: readSession(),
    menus: [],
    menusReady: false,
    menuError: '',
    menusLoading: null
  }),
  getters: {
    authenticated: state => Boolean(state.current?.accessToken),
    displayName: state => state.current?.username || '管理员'
  },
  actions: {
    async signIn(credentials) {
      const data = await login(credentials)
      this.current = {
        ...tokenDetails(data),
        username: credentials.username.trim(),
        idleId: crypto.randomUUID(),
        lastActivityAt: Date.now()
      }
      saveSession(this.current)
      this.menusReady = false
      this.menuError = ''
    },
    async ensureAccessToken(options) {
      if (!refreshers.has(this)) {
        refreshers.set(this, createTokenRefresher({
          getSession: () => this.current,
          setSession: current => { this.current = current; saveSession(current) },
          requestRefresh: refreshTokens,
          expireSession: id => this.expireSession(id),
          revokeToken: logout
        }))
      }
      return refreshers.get(this)(options)
    },
    async expireSession(id) {
      if (this.current?.idleId !== id) return
      const accessToken = this.current.accessToken
      this.clearLocal()
      void logout(accessToken).catch(() => undefined)
      const { default: router, clearMenuRoutes } = await import('./router')
      if (this.current) return
      clearMenuRoutes()
      if (router.currentRoute.value.path !== '/login') {
        await router.replace({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
      }
    },
    async ensureMenus() {
      if (this.menusReady) return this.menus
      if (!this.menusLoading) {
        this.menusLoading = fetchMenus().then(menus => {
          this.menus = menus
          this.menusReady = true
          this.menuError = ''
          return menus
        }).catch(error => {
          this.menuError = error.message || '菜单加载失败'
          throw error
        }).finally(() => { this.menusLoading = null })
      }
      return this.menusLoading
    },
    clearLocal() {
      clearSession()
      this.current = null
      this.menus = []
      this.menusReady = false
      this.menuError = ''
      this.menusLoading = null
    },
    async signOut() {
      const accessToken = this.current?.accessToken
      this.clearLocal()
      if (!accessToken) return
      await logout(accessToken).catch(() => undefined)
    }
  }
})

export function startTokenRefreshMonitor() {
  const session = useSession()
  let timer
  function schedule(retryDelay) {
    clearTimeout(timer)
    const current = session.current
    if (!current?.refreshToken) return
    const refreshAt = current.refreshAt ?? (current.expiresAt ? current.expiresAt - 60000 : null)
    if (!refreshAt) return
    timer = setTimeout(check, retryDelay ?? Math.min(2147483647, Math.max(1000, refreshAt - Date.now())))
  }
  async function check() {
    try { await session.ensureAccessToken(); schedule() }
    catch { schedule(30000) }
  }
  watch(() => [session.current?.idleId, session.current?.refreshAt], () => schedule(), { immediate: true })
  document.addEventListener('visibilitychange', () => { if (!document.hidden) void check() })
  window.addEventListener('focus', () => { void check() })
}
