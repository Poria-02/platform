import cache from '@/plugins/cache'
import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
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
        const tokenResponse = res?.data || res
        const accessToken = tokenResponse?.access_token ?? tokenResponse?.accessToken ?? tokenResponse?.token
        const token = typeof accessToken === 'string' ? accessToken : accessToken?.tokenValue
        if (!token) throw new Error('认证服务未返回 access_token')
        const expiresIn = tokenResponse?.expires_in ?? tokenResponse?.expiresIn
        setToken(token, Number(expiresIn))
        this.token = token
        this.name = userInfo.username?.trim() || ''
        this.nickName = this.name
        useLockStore().unlockScreen()
      })
    },
    getInfo() {
      return getInfo().then(res => {
        const menuTree = res?.data ?? res
        this.menus = Array.isArray(menuTree) ? menuTree : []
        this.permissions = permissionsOf(this.menus)
        // UPMS 菜单接口不返回若依格式的角色；该标识仅用于完成路由守卫初始化。
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
        this.token = ''
        this.roles = []
        this.permissions = []
        this.menus = []
        removeToken()
      })
    }
  }
})

export default useUserStore
