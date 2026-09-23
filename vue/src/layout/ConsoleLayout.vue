<template>
  <div class="console-shell" :class="{ 'sidebar-collapsed': collapsed, 'mobile-open': mobileOpen }">
    <div v-if="mobileOpen" class="mobile-scrim" @click="mobileOpen = false" />
    <aside class="console-sidebar">
      <router-link class="sidebar-brand" to="/index" @click="mobileOpen = false"><span class="brand-mark">P</span><span class="brand-word">PORIA<span>.</span><small>CONTROL CENTER</small></span></router-link>
      <div class="sidebar-caption">工作空间</div>
      <nav class="sidebar-nav" aria-label="主菜单">
        <router-link class="menu-link" to="/index" @click="mobileOpen = false"><span class="menu-symbol">▣</span><span>工作台</span></router-link>
        <div class="sidebar-caption sidebar-caption--modules">功能模块</div>
        <MenuItem v-for="item in menuRoots" :key="item.id || item.path" :item="item" />
      </nav>
      <div class="sidebar-bottom"><span class="sidebar-status-dot" />已连接 Poria Platform</div>
    </aside>
    <div class="console-main">
      <header class="console-header">
        <button class="nav-toggle" type="button" aria-label="切换菜单" @click="toggleSidebar">☰</button>
        <div class="header-location"><span>工作空间</span><span class="breadcrumb-sep">/</span><strong>{{ currentTitle }}</strong></div>
        <div class="header-actions"><time class="header-date">{{ clockText }}</time><span class="header-divider" /><el-dropdown trigger="click" @command="handleCommand"><button type="button" class="account-button"><span class="account-avatar">{{ initial }}</span><span class="account-name">{{ session.displayName }}</span><span>⌄</span></button><template #dropdown><el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu></template></el-dropdown></div>
      </header>
      <div class="workspace-tabs" role="tablist" aria-label="已打开页面">
        <div v-for="tab in tabs" :key="tab.path" class="workspace-tab" :class="{ 'workspace-tab--active': route.path === tab.path }" role="tab" tabindex="0" :aria-selected="route.path === tab.path" @click="openTab(tab.path)" @keydown.enter="openTab(tab.path)" @keydown.space.prevent="openTab(tab.path)">
          <span class="workspace-tab__dot" /><span class="workspace-tab__title">{{ tab.title }}</span>
          <button v-if="tab.path !== '/index'" class="workspace-tab__close" type="button" :aria-label="`关闭${tab.title}`" @click.stop="closeTab(tab.path)" @keydown.enter.stop @keydown.space.stop>×</button>
        </div>
      </div>
      <main class="console-content"><router-view v-slot="{ Component, route: pageRoute }"><KeepAlive><component :is="Component" :key="pageRoute.path" /></KeepAlive></router-view></main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSession } from '@/core/session'
import { now, startClock } from '@/core/clock'
import { clearMenuRoutes } from '@/core/router'
import MenuItem from './MenuItem.vue'

const route = useRoute()
const router = useRouter()
const session = useSession()
const collapsed = ref(false)
const mobileOpen = ref(false)
const tabs = ref([{ path: '/index', title: '工作台' }])
const menuRoots = computed(() => session.menus.filter(item => item.path !== '/index' && String(item.type) !== '1'))
const currentTitle = computed(() => route.meta.title || '工作台')
const initial = computed(() => session.displayName.slice(0, 1).toUpperCase())
const clockText = computed(() => new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', weekday: 'long', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }).format(now.value))
let stopClock

onMounted(() => { stopClock = startClock() })
onUnmounted(() => { stopClock?.() })

function openTab(path) { if (route.path !== path) router.push(path) }
function closeTab(path) {
  const index = tabs.value.findIndex(tab => tab.path === path)
  if (index < 0) return
  tabs.value.splice(index, 1)
  if (route.path === path) router.push(tabs.value[Math.min(index, tabs.value.length - 1)].path)
}

function toggleSidebar() {
  if (window.innerWidth <= 900) mobileOpen.value = !mobileOpen.value
  else collapsed.value = !collapsed.value
}
async function handleCommand(command) {
  if (command !== 'logout') return
  await session.signOut()
  clearMenuRoutes()
  router.replace('/login')
}
watch(() => route.path, path => {
  mobileOpen.value = false
  if (!tabs.value.some(tab => tab.path === path)) tabs.value.push({ path, title: String(route.meta.title || '页面') })
}, { immediate: true })
</script>
