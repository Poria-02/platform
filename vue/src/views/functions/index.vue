<template>
  <div class="functions-page">
    <header class="functions-heading">
      <div>
        <h1>全部功能</h1>
        <p>浏览可用功能，快速进入所需页面。</p>
      </div>
      <div class="functions-actions">
        <router-link class="back-link" to="/index"><span aria-hidden="true">←</span> 返回工作台</router-link>
        <el-button :loading="refreshing" @click="refreshMenus">刷新菜单</el-button>
      </div>
    </header>

    <div class="function-toolbar">
      <nav class="function-views" aria-label="功能视图">
        <button type="button" :class="{ 'is-active': view === 'all' }" :aria-pressed="view === 'all'" @click="view = 'all'"><MenuIcon name="grid" /><span>全部功能</span><span class="view-count">{{ entries.length }}</span></button>
        <button type="button" :class="{ 'is-active': view === 'pinned' }" :aria-pressed="view === 'pinned'" @click="view = 'pinned'"><MenuIcon name="collection" /><span>常用功能</span><span class="view-count">{{ availablePins.length }}</span></button>
      </nav>

      <div class="function-filters">
        <el-input ref="searchRef" v-model="keyword" clearable placeholder="搜索名称、分类或路径" aria-label="搜索功能名称、分类或路径" title="按 / 聚焦搜索，按 Esc 清空关键词" class="function-search" @keydown="handleSearchInputKeydown">
          <template #prefix><MenuIcon name="search" /></template>
          <template #suffix><kbd v-if="!keyword" class="search-key-hint" aria-hidden="true">/</kbd></template>
        </el-input>
        <el-select v-model="category" placeholder="全部分类" aria-label="按分类筛选" class="function-category">
          <el-option label="全部分类" value="" />
          <el-option v-for="item in categories" :key="item" :label="item" :value="item" />
        </el-select>
        <span class="function-total" aria-live="polite">共 {{ filteredEntries.length }} 项功能</span>
      </div>
    </div>

    <div v-if="session.menuError" class="functions-error" role="alert"><span>菜单加载失败，请重试。</span><el-button link :loading="refreshing" @click="refreshMenus">重新加载</el-button></div>

    <section v-for="group in groups" :key="group.title" class="function-group" :aria-label="group.title">
      <div class="function-group__heading"><h2><SearchText :text="group.title" :keyword="keyword" /></h2><span>{{ group.entries.length }}</span></div>
      <div class="function-grid">
        <article v-for="entry in group.entries" :key="entry.path" class="function-card" :class="{ 'is-pinned': pinnedSet.has(entry.path) }">
          <component :is="entry.external ? 'a' : RouterLink" v-bind="entry.external ? { href: entry.path, target: '_blank', rel: 'noopener noreferrer' } : { to: entry.path }" class="function-card__link" :aria-label="`${entry.title}${entry.external ? '（新窗口打开）' : ''}`">
            <span class="function-card__icon"><MenuIcon :name="entry.icon" /></span>
            <span class="function-card__copy"><strong :title="entry.title"><SearchText :text="entry.title" :keyword="keyword" /></strong><span :title="entry.path"><SearchText :text="entry.path" :keyword="keyword" /></span></span>
            <span class="function-card__arrow" aria-hidden="true">{{ entry.external ? '↗' : '→' }}</span>
          </component>
          <button class="function-card__pin" type="button" :aria-pressed="pinnedSet.has(entry.path)" :aria-label="`${pinnedSet.has(entry.path) ? '取消固定' : '固定'}${entry.title}`" :title="pinnedSet.has(entry.path) ? '取消固定' : '固定到常用功能'" @click="togglePin(entry.path)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 3h6l-1 6 4 4v2H6v-2l4-4-1-6Z" :fill="pinnedSet.has(entry.path) ? 'currentColor' : 'none'" /><path d="M12 15v6" /></svg>
          </button>
        </article>
      </div>
    </section>

    <div v-if="!groups.length" class="functions-empty">
      <MenuIcon :name="view === 'pinned' && !keyword && !category ? 'collection' : 'search'" />
      <h2>{{ view === 'pinned' && !keyword && !category ? '还没有常用功能' : '没有匹配的功能' }}</h2>
      <p>{{ view === 'pinned' && !keyword && !category ? '在全部功能中点击图钉，把常用入口放在这里。' : '试试其他名称，或清除筛选条件。' }}</p>
      <el-button v-if="keyword || category" @click="clearFilters">清除筛选</el-button>
      <el-button v-else-if="view === 'pinned'" @click="view = 'all'">浏览全部功能</el-button>
      <el-button v-else :loading="refreshing" @click="refreshMenus">刷新菜单</el-button>
    </div>
    <p class="functions-hint">按 / 搜索，按 Esc 清空关键词。点击卡片进入功能，点击图钉固定常用入口。{{ temporaryPins ? '当前浏览器无法保存设置，固定入口仅在本次访问中保留。' : '常用功能保存在当前浏览器，按账号区分。' }}</p>
  </div>
</template>

<script setup>
import { computed, onActivated, onDeactivated, onMounted, onUnmounted, reactive, ref, toRefs, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useSession } from '@/core/session'
import { clearMenuRoutes, installMenuRoutes } from '@/core/router'
import { buildFunctionCatalog, filterFunctions, readPinnedFunctions } from '@/core/functionCatalog'
import MenuIcon from '@/ui/MenuIcon.vue'
import SearchText from '@/components/SearchText.vue'
import { useListRouteState } from '@/core/listRouteState'

const session = useSession()
const filters = reactive({ keyword: '', category: '', view: 'all' })
const { keyword, category, view } = toRefs(filters)
const searchRef = ref()
let active = true
const refreshing = ref(false)
const pinnedPaths = ref([])
const temporaryPins = ref(false)
const storageKey = computed(() => session.current?.username ? `poria.console.pinned-functions.${encodeURIComponent(session.current.username)}` : '')
const entries = computed(() => buildFunctionCatalog(session.menus, { includeProfile: session.hasPermission('sys_user_profile') }))
const categories = computed(() => [...new Set(entries.value.map(entry => entry.category))])
const pinnedSet = computed(() => new Set(pinnedPaths.value))
const availablePins = computed(() => entries.value.filter(entry => pinnedSet.value.has(entry.path)))
const filteredEntries = computed(() => filterFunctions(entries.value, {
  keyword: keyword.value, category: category.value, pinnedOnly: view.value === 'pinned', pinnedPaths: pinnedPaths.value
}))
const groups = computed(() => {
  const grouped = new Map()
  for (const entry of filteredEntries.value) {
    if (!grouped.has(entry.category)) grouped.set(entry.category, { title: entry.category, entries: [] })
    grouped.get(entry.category).entries.push(entry)
  }
  return [...grouped.values()]
})

function restorePins() {
  try {
    pinnedPaths.value = readPinnedFunctions(window.localStorage, storageKey.value)
    temporaryPins.value = false
  } catch {
    pinnedPaths.value = []
    temporaryPins.value = true
  }
}

function togglePin(path) {
  if (!storageKey.value || !entries.value.some(entry => entry.path === path)) return
  if (pinnedSet.value.has(path)) pinnedPaths.value = pinnedPaths.value.filter(item => item !== path)
  else {
    if (pinnedPaths.value.length >= 100) { ElMessage.info('最多固定 100 个功能'); return }
    pinnedPaths.value = [...pinnedPaths.value, path]
  }
  try {
    window.localStorage.setItem(storageKey.value, JSON.stringify(pinnedPaths.value))
    temporaryPins.value = false
  } catch { temporaryPins.value = true }
}

function handleStorage(event) {
  if (event.key === storageKey.value || event.key === null) restorePins()
}
function clearFilters() { keyword.value = ''; category.value = '' }

function handleSearchShortcut(event) {
  if (!active || event.defaultPrevented || event.key !== '/' || event.repeat || event.isComposing || event.ctrlKey || event.metaKey || event.altKey) return
  const editable = event.target?.closest?.('input, textarea, select, [contenteditable]:not([contenteditable="false"]), [role="textbox"]')
  if (editable || Array.from(document.querySelectorAll('[aria-modal="true"]')).some(element => element.getClientRects().length)) return
  event.preventDefault()
  searchRef.value?.focus()
}

function handleSearchInputKeydown(event) {
  if (event.key !== 'Escape' || event.isComposing) return
  event.preventDefault()
  event.stopPropagation()
  keyword.value = ''
}

async function refreshMenus() {
  const sessionId = session.current?.idleId
  if (refreshing.value || !sessionId) return
  refreshing.value = true
  try {
    const menus = await session.ensureMenus({ force: true })
    if (session.current?.idleId !== sessionId) return
    clearMenuRoutes()
    installMenuRoutes(menus)
  } catch { /* 会话保存菜单错误，页面提供重试入口。 */ }
  finally { refreshing.value = false }
}

watch(storageKey, () => { clearFilters(); view.value = 'all'; restorePins() }, { immediate: true })
const { syncListRoute } = useListRouteState({ query: filters, reload: () => {}, allowedValues: { view: ['all', 'pinned'] } })
watch([keyword, category, view], () => { void syncListRoute().catch(() => {}) })
watch(categories, available => { if (category.value && !available.includes(category.value)) category.value = '' }, { immediate: true })
onMounted(() => {
  window.addEventListener('storage', handleStorage)
  window.addEventListener('keydown', handleSearchShortcut)
  void syncListRoute().catch(() => {})
})
onActivated(() => { active = true })
onDeactivated(() => { active = false })
onUnmounted(() => {
  window.removeEventListener('storage', handleStorage)
  window.removeEventListener('keydown', handleSearchShortcut)
})
</script>

<style scoped>
.functions-page { width: 100%; padding: 0 0 20px; container-type: inline-size; container-name: functions; }
.functions-heading { display: flex; align-items: center; justify-content: space-between; gap: 24px; margin-bottom: 24px; }
.functions-heading h1 { margin: 0 0 7px; color: var(--ink); font-size: 27px; line-height: 1.4; font-weight: 650; letter-spacing: -.025em; }
.functions-heading p { margin: 0; color: var(--muted); font-size: 13px; line-height: 1.6; }
.functions-actions { display: flex; align-items: center; flex-shrink: 0; gap: 10px; }
.back-link { display: inline-flex; align-items: center; gap: 7px; min-height: 32px; padding: 0 10px; border-radius: 3px; color: #777; font-size: 12px; }
.back-link:hover { background: #ededed; color: var(--ink); }
.functions-actions :deep(.el-button) { color: #666; font-size: 12px; }
.function-toolbar { display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 14px 20px; margin-bottom: 26px; }
.function-views { display: inline-flex; flex-shrink: 0; align-items: center; gap: 3px; padding: 3px; border: 1px solid #e7e7e7; border-radius: 5px; background: #efefef; }
.function-views button { display: inline-flex; align-items: center; gap: 8px; min-height: 30px; padding: 0 12px; border: 1px solid transparent; border-radius: 3px; background: transparent; color: #888; font-size: 12px; cursor: pointer; }
.function-views button:hover, .function-views button.is-active { color: var(--ink); }
.function-views button.is-active { border-color: #e2e2e2; background: #fff; font-weight: 600; box-shadow: 0 1px 2px #00000004; }
.view-count { color: #999; font-size: 11px; font-variant-numeric: tabular-nums; }
.function-filters { display: flex; flex: 1; align-items: center; justify-content: flex-end; gap: 10px; min-width: min(500px, 100%); }
.function-search { flex: 0 1 360px; min-width: 160px; }
.function-category { flex: 0 0 154px; }
.function-search :deep(.el-input__wrapper), .function-category :deep(.el-select__wrapper) { min-height: 36px; border-radius: 4px; background: #fff; }
.function-search :deep(.el-input__inner), .function-category :deep(.el-select__placeholder) { font-size: 12px; }
.search-key-hint { min-width: 18px; padding: 0 4px; border: 1px solid #e7e7e7; border-radius: 3px; background: #f7f7f7; color: #999; font-family: inherit; font-size: 11px; line-height: 18px; text-align: center; }
.function-total { flex-shrink: 0; color: var(--muted); font-size: 11px; font-variant-numeric: tabular-nums; }
.functions-error { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 24px; padding: 12px 16px; border-radius: 5px; background: #fff1ed; color: #a94637; font-size: 13px; }
.function-group + .function-group { margin-top: 25px; }
.function-group__heading { display: flex; align-items: center; gap: 9px; margin-bottom: 11px; }
.function-group__heading::after { height: 1px; flex: 1; margin-left: 3px; background: #e8e8e8; content: ''; }
.function-group__heading h2 { margin: 0; color: #666; font-size: 12px; line-height: 20px; font-weight: 600; }
.function-group__heading > span { color: #aaa; font-size: 10px; font-variant-numeric: tabular-nums; }
.function-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; }
.function-card { display: flex; align-items: center; min-width: 0; min-height: 80px; overflow: hidden; border: 1px solid #e7e7e7; border-radius: 5px; background: #fff; transition: border-color .16s ease, background .16s ease; }
.function-card:hover { border-color: #c9c9c9; background: #fdfdfd; }
.function-card__link { display: flex; flex: 1; align-items: center; gap: 10px; align-self: stretch; min-width: 0; padding: 15px 8px 15px 14px; }
.function-card__icon { display: grid; flex: 0 0 33px; height: 33px; place-items: center; border-radius: 4px; background: #f5f5f5; color: #777; font-size: 18px; }
.function-card__copy { display: flex; flex: 1; flex-direction: column; gap: 5px; min-width: 0; }
.function-card__copy strong { overflow: hidden; color: var(--ink); font-size: 13px; font-weight: 600; text-overflow: ellipsis; white-space: nowrap; }
.function-card__copy > span { overflow: hidden; color: #999; font-size: 11px; line-height: 16px; text-overflow: ellipsis; white-space: nowrap; }
.function-card__arrow { flex-shrink: 0; color: #aaa; font-size: 16px; }
.function-card:hover .function-card__arrow { color: #666; }
.function-card__pin { display: grid; flex: 0 0 28px; height: 30px; margin-right: 8px; place-items: center; padding: 0; border: 0; border-radius: 3px; background: transparent; color: #aaa; cursor: pointer; }
.function-card__pin svg { width: 14px; height: 14px; }
.function-card__pin:hover { color: #555; background: #f2f2f2; }
.is-pinned .function-card__pin { background: #f2f2f2; color: #666; }
.function-card__link:focus-visible, .function-card__pin:focus-visible { outline: 2px solid var(--accent); outline-offset: -3px; }
.function-views button:focus-visible, .back-link:focus-visible { outline: 2px solid var(--accent); outline-offset: 3px; }
.functions-empty { display: flex; flex-direction: column; align-items: center; min-height: 260px; justify-content: center; padding: 32px 20px; border: 1px dashed var(--line); border-radius: 5px; text-align: center; }
.functions-empty > .el-icon { margin-bottom: 12px; color: #999; font-size: 28px; }
.functions-empty h2 { margin: 0 0 10px; color: #555; font-size: 16px; font-weight: 600; }
.functions-empty p { margin: 0 0 18px; color: var(--muted); font-size: 13px; line-height: 1.7; }
.functions-hint { margin: 24px 0 0; color: #999; font-size: 11px; line-height: 1.8; }
@container functions (max-width: 1100px) { .function-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); } }
@container functions (max-width: 800px) { .function-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@container functions (max-width: 650px) {
  .functions-heading { align-items: flex-start; flex-direction: column; gap: 14px; }
  .functions-heading h1 { font-size: 25px; }
  .function-toolbar { gap: 14px; }
  .function-filters { flex-wrap: wrap; justify-content: flex-start; min-width: 100%; }
  .function-search { flex: 1 1 100%; min-width: 0; }
  .function-category { flex: 1; min-width: 0; }
  .function-views button { padding: 0 11px; }
}
@container functions (max-width: 520px) { .function-grid { grid-template-columns: 1fr; } }
@media (pointer: coarse), (max-width: 700px) {
  .function-card__pin { flex-basis: 44px; height: 44px; }
  .function-card__pin svg { width: 16px; height: 16px; }
  .function-views button, .back-link { min-height: 44px; }
  .functions-actions :deep(.el-button) { min-height: 44px; }
  .function-search :deep(.el-input__wrapper), .function-category :deep(.el-select__wrapper) { min-height: 44px; }
}
</style>
