import cache from '@/plugins/cache'
import {
  login,
  logout,
  getInfo
} from '@/views/login/api'
import {
  getToken,
  setToken,
  removeToken
} from '@/utils/auth'
import useLockStore from '@/store/modules/lock'
import defAva from '@/assets/images/profile.jpg'

function permissionsOf(items = []) {
  return items.flatMap(item => [item.permission, ...permissionsOf(item.children || [])]).filter(Boolean)
}

const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    id: '',
    name: '',
    nickName: '',
    avatar: defAva,
    roles: [],
    permissions: [],
    menus: []
  }),
  actions: {
    login(userInfo) {
      return login(userInfo).then(res => {
        // Auth 使用 R.ok(...) 包装令牌响应。实际响应中的 accessToken 是
        // OAuth2AccessToken 对象，Bearer 字符串位于 tokenValue。
        const tokenResponse = res?.data || res
        const accessToken = tokenResponse?.access_token ?? tokenResponse?.accessToken ?? tokenResponse?.token
        const token = typeof accessToken === 'string' ? accessToken : accessToken?.tokenValue
        if (!token) throw new Error('认证服务未返回 access_token')
        const expiresIn = tokenResponse?.expires_in ??
          tokenResponse?.expiresIn ??
          (accessToken?.expiresAt ? Math.floor((new Date(accessToken.expiresAt).getTime() - Date.now()) / 1000) : undefined)
        setToken(token, Number(expiresIn))
        this.token = token
        this.name = userInfo.username
        this.nickName = userInfo.username
        useLockStore().unlockScreen()
      })
    },
    getInfo() {
      return getInfo().then(res => {
        this.menus = Array.isArray(res.data) ? res.data : (Array.isArray(res) ? res : [])
        this.permissions = permissionsOf(this.menus)
        // 当前后端菜单接口不返回角色；该标记仅避免路由守卫重复拉取菜单，
        // 不参与任何页面或接口的权限判定。
        this.roles = ['AUTHENTICATED']
        cache.session.set('pwrChrtype', null)
        return {
          menus: this.menus,
          roles: this.roles,
          permissions: this.permissions
        }
      })
    },
    logOut() {
      return logout().catch(() => {}).finally(() => {
        this.token = '';
        this.roles = [];
        this.permissions = [];
        this.menus = [];
        removeToken()
      })
    }
  }
})
export default useUserStore
