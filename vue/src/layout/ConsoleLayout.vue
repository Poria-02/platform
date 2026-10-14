<template>
  <div class="console-shell" :class="{ 'sidebar-collapsed': collapsed, 'mobile-open': mobileOpen }">
    <Transition name="panel-fade"><div v-if="mobileOpen" class="mobile-scrim" @click="mobileOpen = false" /></Transition>
    <aside class="console-sidebar">
      <router-link class="sidebar-brand" to="/index" @click="mobileOpen = false"><span class="brand-mark" aria-hidden="true">P</span><span class="brand-word">PORIA<small>管理工作台</small></span></router-link>
      <nav class="sidebar-nav" aria-label="主菜单">
        <router-link class="menu-link" to="/index" @click="mobileOpen = false"><span class="menu-symbol"><MenuIcon name="house" /></span><span>工作台</span></router-link>
        <div v-if="session.menuError" class="sidebar-menu-error" role="alert"><span>菜单加载失败</span><el-button link :loading="retryingMenus" @click="retryMenus">重试</el-button></div>
        <MenuItem v-for="item in menuRoots" :key="item.id || item.path" :item="item" />
      </nav>
      <div class="sidebar-weather"><WeatherWidget /></div>
      <div class="sidebar-account">
        <router-link class="sidebar-account__profile" to="/profile/index" aria-label="打开个人中心" title="个人中心" @click="mobileOpen = false">
          <span class="account-avatar">{{ initial }}</span>
          <span class="sidebar-account__info"><strong>{{ session.displayName }}</strong><small>个人中心</small></span>
        </router-link>
        <button class="sidebar-account__logout" type="button" aria-label="退出登录" title="退出登录" @click="handleCommand('logout')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 4H4v16h5M14 8l4 4-4 4M8 12h12" /></svg>
        </button>
      </div>
    </aside>
    <div class="console-main">
      <header class="console-header">
        <button class="nav-toggle" type="button" aria-label="切换菜单" @click="toggleSidebar">☰</button>
        <div class="header-location"><span>工作空间</span><span class="breadcrumb-sep">/</span><strong>{{ currentTitle }}</strong></div>
        <MessagePanel />
      </header>
      <div class="workspace-tabs" role="tablist" aria-label="已打开页面">
        <div v-for="tab in tabs" :key="tab.path" class="workspace-tab" :class="{ 'workspace-tab--active': route.path === tab.path }" role="tab" tabindex="0" :aria-selected="route.path === tab.path" @click="openTab(tab.path)" @contextmenu.prevent="openContextMenu($event, tab.path)" @keydown.enter="openTab(tab.path)" @keydown.space.prevent="openTab(tab.path)">
          <span class="workspace-tab__dot" /><span class="workspace-tab__title">{{ tab.title }}</span>
          <button v-if="tab.path !== '/index'" class="workspace-tab__close" type="button" :aria-label="`关闭${tab.title}`" @click.stop="closeTab(tab.path)" @keydown.enter.stop @keydown.space.stop>×</button>
        </div>
      </div>
      <main class="console-content">
        <router-view v-if="session.authenticated" v-slot="{ Component, route: pageRoute }">
          <Transition name="page-fade" mode="out-in" appear>
            <KeepAlive :key="session.current?.idleId" :include="cachedPageNames" :max="12">
              <component :is="pageWrapper(pageRoute.path)" :key="pageRoute.path"><component :is="Component" /></component>
            </KeepAlive>
          </Transition>
        </router-view>
      </main>
    </div>
    <Teleport to="body">
      <Transition name="panel-fade">
        <div v-if="contextMenu.open" ref="contextMenuRef" class="workspace-tab-menu" role="menu" aria-label="标签页操作" :style="{ left: `${contextMenu.x}px`, top: `${contextMenu.y}px` }" @contextmenu.prevent>
          <button type="button" role="menuitem" :disabled="contextMenu.path === '/index'" @click="closeContextTab">关闭页面</button>
          <button type="button" role="menuitem" :disabled="tabs.length === 1" @click="closeAllTabs">关闭所有页面</button>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, markRaw, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSession } from '@/core/session'
import { startClock } from '@/core/clock'
import { installMenuRoutes } from '@/core/router'
import { consoleMenus } from '@/core/menu'
import WeatherWidget from '@/components/WeatherWidget.vue'
import RoutePage from '@/components/RoutePage.vue'
import MessagePanel from '@/components/MessagePanel.vue'
import MenuItem from './MenuItem.vue'
import MenuIcon from '@/ui/MenuIcon.vue'

const route = useRoute()
const router = useRouter()
const session = useSession()
const collapsed = ref(false)
const mobileOpen = ref(false)
const tabs = ref([{ path: '/index', fullPath: '/index', title: '工作台' }])
const retryingMenus = ref(false)
const pageWrappers = new Map()
const pageName = path => `Page_${Array.from(path, character => character.codePointAt(0).toString(16)).join('_')}`
const cachedPageNames = computed(() => tabs.value.map(tab => pageName(tab.path)))
function pageWrapper(path) {
  if (!pageWrappers.has(path)) {
    pageWrappers.set(path, markRaw(defineComponent({
      name: pageName(path),
      setup: (_props, { slots }) => () => h(RoutePage, null, slots)
    })))
  }
  return pageWrappers.get(path)
}
async function retryMenus() {
  if (retryingMenus.value) return
  retryingMenus.value = true
  try { installMenuRoutes(await session.ensureMenus()) }
  catch { /* 保留菜单错误提示，允许再次重试。 */ }
  finally { retryingMenus.value = false }
}
const contextMenu = reactive({ open: false, path: '', x: 0, y: 0 })
const contextMenuRef = ref(null)
const menuRoots = computed(() => consoleMenus(session.menus))
const currentTitle = computed(() => route.meta.title || '工作台')
const initial = computed(() => session.displayName.slice(0, 1).toUpperCase())
let stopClock

onMounted(() => {
  stopClock = startClock()
  window.addEventListener('pointerdown', dismissContextMenu)
  window.addEventListener('keydown', handleContextKeydown)
  window.addEventListener('scroll', dismissContextMenu, true)
})
onUnmounted(() => {
  stopClock?.()
  window.removeEventListener('pointerdown', dismissContextMenu)
  window.removeEventListener('keydown', handleContextKeydown)
  window.removeEventListener('scroll', dismissContextMenu, true)
})

function openTab(path) {
  const target = tabs.value.find(tab => tab.path === path)?.fullPath || path
  if (route.fullPath !== target) router.push(target)
}
async function closeTab(path) {
  if (path === '/index') return
  const index = tabs.value.findIndex(tab => tab.path === path)
  if (index < 0) return
  if (route.path === path) {
    const next = tabs.value[index + 1] || tabs.value[index - 1]
    await router.push(next.fullPath || next.path)
    if (route.path === path) return
  }
  tabs.value = tabs.value.filter(tab => tab.path !== path)
  pageWrappers.delete(path)
}
function openContextMenu(event, path) {
  const { clientX, clientY } = event
  contextMenu.path = path
  contextMenu.x = clientX
  contextMenu.y = clientY
  contextMenu.open = true
  nextTick(() => {
    const rect = contextMenuRef.value?.getBoundingClientRect()
    if (!rect) return
    contextMenu.x = Math.max(8, Math.min(clientX, window.innerWidth - rect.width - 8))
    contextMenu.y = Math.max(8, Math.min(clientY, window.innerHeight - rect.height - 8))
  })
}
function dismissContextMenu(event) {
  if (!event || !contextMenuRef.value?.contains(event.target)) contextMenu.open = false
}
function handleContextKeydown(event) { if (event.key === 'Escape') dismissContextMenu() }
function closeContextTab() {
  const path = contextMenu.path
  contextMenu.open = false
  closeTab(path)
}
async function closeAllTabs() {
  contextMenu.open = false
  if (route.path !== '/index') await router.push('/index')
  if (route.path !== '/index') return
  tabs.value = [tabs.value.find(tab => tab.path === '/index')]
  for (const path of pageWrappers.keys()) if (path !== '/index') pageWrappers.delete(path)
}

function toggleSidebar() {
  if (window.innerWidth <= 900) mobileOpen.value = !mobileOpen.value
  else collapsed.value = !collapsed.value
}
async function handleCommand(command) {
  if (command !== 'logout' || !session.authenticated) return
  await session.signOut()
}
watch(() => session.current?.idleId, () => {
  tabs.value = [{ path: '/index', fullPath: '/index', title: '工作台' }]
  pageWrappers.clear()
  contextMenu.open = false
})
watch(() => route.fullPath, fullPath => {
  const path = route.path
  mobileOpen.value = false
  contextMenu.open = false
  const tab = tabs.value.find(tab => tab.path === path)
  if (tab) tab.fullPath = fullPath
  else tabs.value.push({ path, fullPath, title: String(route.meta.title || '页面') })
}, { immediate: true })
</script>

<style scoped>
.sidebar-menu-error { display: flex; align-items: center; justify-content: space-between; gap: 8px; padding: 8px 12px; color: #a94637; font-size: 12px; }
</style>
