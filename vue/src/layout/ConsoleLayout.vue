<template>
  <div class="console-shell" :class="{ 'sidebar-collapsed': collapsed, 'mobile-open': mobileOpen }">
    <div v-if="mobileOpen" class="mobile-scrim" @click="mobileOpen = false" />
    <aside class="console-sidebar">
      <router-link class="sidebar-brand" to="/index" @click="mobileOpen = false"><span class="brand-mark" aria-hidden="true"><i /><i /><i /><i /></span><span class="brand-word">PORIA<small>管理工作台</small></span></router-link>
      <nav class="sidebar-nav" aria-label="主菜单">
        <router-link class="menu-link" to="/index" @click="mobileOpen = false"><span class="menu-symbol"><MenuIcon name="house" /></span><span>工作台</span></router-link>
        <MenuItem v-for="item in menuRoots" :key="item.id || item.path" :item="item" />
      </nav>
      <div class="sidebar-bottom"><span class="sidebar-status-dot" />Poria Platform</div>
    </aside>
    <div class="console-main">
      <header class="console-header">
        <button class="nav-toggle" type="button" aria-label="切换菜单" @click="toggleSidebar">☰</button>
        <div class="header-location"><span>工作空间</span><span class="breadcrumb-sep">/</span><strong>{{ currentTitle }}</strong></div>
        <div class="header-actions"><time class="header-date">{{ clockText }}</time><span class="header-divider" /><el-dropdown trigger="click" @command="handleCommand"><button type="button" class="account-button"><span class="account-avatar">{{ initial }}</span><span class="account-name">{{ session.displayName }}</span><span>⌄</span></button><template #dropdown><el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu></template></el-dropdown></div>
      </header>
      <div class="workspace-tabs" role="tablist" aria-label="已打开页面">
        <div v-for="tab in tabs" :key="tab.path" class="workspace-tab" :class="{ 'workspace-tab--active': route.path === tab.path }" role="tab" tabindex="0" :aria-selected="route.path === tab.path" @click="openTab(tab.path)" @contextmenu.prevent="openContextMenu($event, tab.path)" @keydown.enter="openTab(tab.path)" @keydown.space.prevent="openTab(tab.path)">
          <span class="workspace-tab__dot" /><span class="workspace-tab__title">{{ tab.title }}</span>
          <button v-if="tab.path !== '/index'" class="workspace-tab__close" type="button" :aria-label="`关闭${tab.title}`" @click.stop="closeTab(tab.path)" @keydown.enter.stop @keydown.space.stop>×</button>
        </div>
      </div>
      <main class="console-content"><router-view v-slot="{ Component, route: pageRoute }"><KeepAlive><component :is="Component" :key="pageRoute.path" /></KeepAlive></router-view></main>
    </div>
    <Teleport to="body">
      <div v-if="contextMenu.open" ref="contextMenuRef" class="workspace-tab-menu" role="menu" aria-label="标签页操作" :style="{ left: `${contextMenu.x}px`, top: `${contextMenu.y}px` }" @contextmenu.prevent>
        <button type="button" role="menuitem" :disabled="contextMenu.path === '/index'" @click="closeContextTab">关闭页面</button>
        <button type="button" role="menuitem" :disabled="tabs.length === 1" @click="closeAllTabs">关闭所有页面</button>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSession } from '@/core/session'
import { now, startClock } from '@/core/clock'
import { clearMenuRoutes } from '@/core/router'
import MenuItem from './MenuItem.vue'
import MenuIcon from '@/ui/MenuIcon.vue'

const route = useRoute()
const router = useRouter()
const session = useSession()
const collapsed = ref(false)
const mobileOpen = ref(false)
const tabs = ref([{ path: '/index', title: '工作台' }])
const contextMenu = reactive({ open: false, path: '', x: 0, y: 0 })
const contextMenuRef = ref(null)
const menuRoots = computed(() => session.menus.filter(item => item.path !== '/index' && String(item.type) !== '1'))
const currentTitle = computed(() => route.meta.title || '工作台')
const initial = computed(() => session.displayName.slice(0, 1).toUpperCase())
const clockText = computed(() => new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', weekday: 'long', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }).format(now.value))
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

function openTab(path) { if (route.path !== path) router.push(path) }
function closeTab(path) {
  if (path === '/index') return
  const index = tabs.value.findIndex(tab => tab.path === path)
  if (index < 0) return
  tabs.value.splice(index, 1)
  if (route.path === path) router.push(tabs.value[Math.min(index, tabs.value.length - 1)].path)
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
function closeAllTabs() {
  contextMenu.open = false
  tabs.value = [tabs.value.find(tab => tab.path === '/index')]
  if (route.path !== '/index') router.push('/index')
}

function toggleSidebar() {
  if (window.innerWidth <= 900) mobileOpen.value = !mobileOpen.value
  else collapsed.value = !collapsed.value
}
async function handleCommand(command) {
  if (command !== 'logout') return
  try {
    await session.signOut()
    clearMenuRoutes()
    router.replace('/login')
  } catch { /* HTTP 层显示令牌删除失败的原因。 */ }
}
watch(() => route.path, path => {
  mobileOpen.value = false
  contextMenu.open = false
  if (!tabs.value.some(tab => tab.path === path)) tabs.value.push({ path, title: String(route.meta.title || '页面') })
}, { immediate: true })
</script>
