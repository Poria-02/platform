import { defineStore } from 'pinia'
import { login, logout } from '@/views/login/api'
import { fetchMenus } from './menu'
import { clearSession, readSession, saveSession } from './token'

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
      const result = await login(credentials)
      const data = result
      const value = data?.access_token ?? data?.accessToken ?? data?.token
      const accessToken = typeof value === 'string' ? value : value?.tokenValue
      if (!accessToken) throw new Error('认证服务未返回 access_token')
      const expires = Number(data?.expires_in ?? data?.expiresIn ?? 0)
      this.current = {
        accessToken,
        username: credentials.username.trim(),
        expiresAt: expires > 0 ? Date.now() + expires * 1000 : null
      }
      saveSession(this.current)
      this.menusReady = false
      this.menuError = ''
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
    async signOut() {
      try { await logout() } catch { /* 本地会话仍要清除 */ }
      clearSession()
      this.current = null
      this.menus = []
      this.menusReady = false
      this.menuError = ''
    }
  }
})
