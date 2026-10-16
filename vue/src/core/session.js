import { defineStore } from 'pinia'
import { watch } from 'vue'
import { login, logout, refreshTokens } from '@/views/login/api'
import { fetchMenus } from './menu'
import { clearMenuCache, clearSession, readMenuCache, readSession, saveMenuCache, saveSession, tokenDetails } from './token'
import { createTokenRefresher } from './refresh'
import { invalidateDictionaries } from './dictionaries'

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
    displayName: state => state.current?.username || '管理员',
    permissions: state => state.current?.permissions || []
  },
  actions: {
    hasPermission(permission) {
      return !permission || this.permissions.includes(permission)
    },
    async signIn(credentials) {
      const data = await login(credentials)
      this.current = {
        ...tokenDetails(data),
        username: credentials.username.trim(),
        idleId: crypto.randomUUID(),
        lastActivityAt: Date.now()
      }
      saveSession(this.current)
      clearMenuCache()
      this.menus = []
      this.menusReady = false
      this.menuError = ''
      this.menusLoading = null
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
      await this.redirectToLogin()
    },
    async redirectToLogin() {
      const { default: router, clearMenuRoutes } = await import('./router')
      if (this.current) return
      clearMenuRoutes()
      if (router.currentRoute.value.path !== '/login') {
        await router.replace('/login')
      }
    },
    async ensureMenus({ force = false } = {}) {
      const sessionId = this.current?.idleId
      if (!sessionId) return []
      if (force) {
        clearMenuCache()
        this.menusReady = false
      } else {
        if (this.menusReady) return this.menus
        const cached = readMenuCache(this.current)
        if (cached !== null) {
          this.menus = cached
          this.menusReady = true
          this.menuError = ''
          return cached
        }
      }
      if (!this.menusLoading) {
        const operation = fetchMenus().then(menus => {
          if (this.current?.idleId !== sessionId) return []
          this.menus = menus
          this.menusReady = true
          this.menuError = ''
          saveMenuCache(sessionId, menus)
          return menus
        }).catch(error => {
          if (this.current?.idleId !== sessionId) return []
          this.menuError = error.message || '菜单加载失败'
          throw error
        }).finally(() => { if (this.menusLoading === operation) this.menusLoading = null })
        this.menusLoading = operation
      }
      return this.menusLoading
    },
    clearLocal() {
      invalidateDictionaries()
      clearSession(this.current?.idleId)
      refreshers.delete(this)
      this.current = null
      this.menus = []
      this.menusReady = false
      this.menuError = ''
      this.menusLoading = null
    },
    async signOut() {
      const accessToken = this.current?.accessToken
      this.clearLocal()
      if (accessToken) void logout(accessToken).catch(() => undefined)
      await this.redirectToLogin()
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

export function startPageSessionMonitor() {
  const session = useSession()
  window.addEventListener('pageshow', event => {
    // 从浏览器前进/后退缓存恢复的页面也不复用旧账号。
    if (event.persisted && session.authenticated) void session.signOut()
  })
}
