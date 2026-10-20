<template>
  <div class="header-messages">
    <el-badge :value="center.unread" :max="99" :hidden="!center.unread"><button class="header-message-button" type="button" aria-label="查看公告与站内信" title="公告与站内信" @click="open = true"><MenuIcon name="bell" /><span>公告</span></button></el-badge>
    <el-drawer v-model="open" class="message-panel-drawer" title="通知与公告" direction="rtl" size="min(460px, 100vw)" header-class="message-panel-header" body-class="message-panel-body" append-to-body @opened="refresh">
      <div class="message-panel-toolbar">
        <el-tabs v-model="active" @tab-change="load">
          <el-tab-pane name="announcement"><template #label><span class="message-panel-tab">公告<small v-if="center.announcementUnread" class="message-panel-count">{{ center.announcementUnread > 99 ? '99+' : center.announcementUnread }}</small></span></template></el-tab-pane>
          <el-tab-pane name="station"><template #label><span class="message-panel-tab">站内信<small v-if="center.stationUnread" class="message-panel-count">{{ center.stationUnread > 99 ? '99+' : center.stationUnread }}</small></span></template></el-tab-pane>
        </el-tabs>
        <el-button class="message-panel-refresh" link :loading="loading" aria-label="刷新消息" title="刷新消息" @click="refresh"><el-icon><Refresh /></el-icon></el-button>
      </div>
      <div v-if="error" class="resource-error" role="alert"><span>{{ error }}</span><el-button link @click="load">重试</el-button></div>
      <div v-loading="loading" class="message-panel-list" :aria-busy="loading">
        <button v-for="item in rows" :key="item.id" class="message-panel-item" type="button" :disabled="opening" @click="show(item)"><span class="message-panel-item-title"><strong>{{ item.title }}</strong><span v-if="isUnread(item)" class="message-panel-dot" /></span><time>{{ date(item.createTime) }}</time><span class="message-panel-preview">{{ item.content }}</span></button>
        <p v-if="!loading && !rows.length" class="message-panel-empty">{{ active === 'announcement' ? '暂无公告' : '暂无站内信' }}</p>
      </div>
      <div class="message-panel-actions"><el-button link :disabled="opening" @click="show()">查看全部{{ active === 'announcement' ? '公告' : '站内信' }}</el-button></div>
    </el-drawer>
  </div>
</template>
<script setup>
import { ElBadge } from 'element-plus/es/components/badge/index.mjs'
import { ElDrawer } from 'element-plus/es/components/drawer/index.mjs'
import { ElTabs, ElTabPane } from 'element-plus/es/components/tabs/index.mjs'
import 'element-plus/es/components/badge/style/css.mjs'
import 'element-plus/es/components/drawer/style/css.mjs'
import 'element-plus/es/components/tabs/style/css.mjs'
import { onBeforeUnmount, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { useSession } from '@/core/session'
import { useMessageCenter } from '@/core/messageCenter'
import MenuIcon from '@/ui/MenuIcon.vue'
import * as station from '@/views/message/station-letter/api'
import * as announcement from '@/views/message/announcement/api'

const router = useRouter()
const session = useSession()
const center = useMessageCenter()
const open = ref(false)
const active = ref('announcement')
const rows = ref([])
const loading = ref(false)
const opening = ref(false)
const error = ref('')
let sequence = 0
const isUnread = item => item.status === 0
const date = value => value ? new Date(value).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : ''
async function load() {
  const current = ++sequence
  const sessionId = session.current?.idleId
  loading.value = true
  rows.value = []
  error.value = ''
  try {
    const api = active.value === 'announcement' ? announcement : station
    const page = await api.list({ current: 1, size: 8 })
    if (current === sequence && session.current?.idleId === sessionId) rows.value = page?.records || []
  } catch (cause) { if (current === sequence) error.value = cause.message || '消息加载失败' }
  finally { if (current === sequence) loading.value = false }
}
function refresh() { void center.refresh(); void load() }
async function show(item) {
  if (opening.value) return
  const sessionId = session.current?.idleId
  const kind = active.value
  opening.value = true
  error.value = ''
  try {
    if (item && isUnread(item)) {
      await (kind === 'announcement' ? announcement : station).read(item.id)
      if (session.current?.idleId !== sessionId) return
      item.status = 1
      void center.refresh()
    }
    if (session.current?.idleId !== sessionId) return
    await router.push({ path: kind === 'announcement' ? '/message/announcement/index' : '/message/station-letter/index', query: item ? { id: item.id } : {} })
    open.value = false
  } catch (cause) { if (session.current?.idleId === sessionId) error.value = cause.message || '消息打开失败' }
  finally { opening.value = false }
}
watch(() => session.current?.idleId, () => { sequence++; open.value = false; rows.value = []; error.value = ''; center.reset(); if (session.authenticated) void center.refresh() }, { immediate: true })
onBeforeUnmount(() => { sequence++ })
</script>
<style scoped>
.header-messages { margin-left: auto; }
.header-message-button { display: flex; align-items: center; gap: 7px; height: 34px; padding: 0 12px; border: 1px solid #dfe7f1; border-radius: 6px; background: #fff; color: #527092; cursor: pointer; }
.header-message-button:hover { color: #3273ea; background: #f3f7ff; }
.header-message-button svg { width: 17px; height: 17px; }
:global(.message-panel-drawer .message-panel-header) { height: 50px; flex-shrink: 0; margin-bottom: 0; padding: 0 18px; border-bottom: 1px solid #edf1f5; color: #42516a; }
:global(.message-panel-drawer .el-drawer__title) { font-size: 14px; font-weight: 600; }
:global(.message-panel-drawer .message-panel-body) { padding: 16px 18px; }
.message-panel-toolbar { display: flex; align-items: center; gap: 12px; padding-bottom: 8px; border-bottom: 1px solid #edf1f5; }
.message-panel-toolbar .el-tabs { flex: 1; min-width: 0; }
.message-panel-toolbar :deep(.el-tabs__header) { margin: 0; }
.message-panel-toolbar :deep(.el-tabs__nav-wrap::after), .message-panel-toolbar :deep(.el-tabs__active-bar) { display: none; }
.message-panel-toolbar :deep(.el-tabs__nav) { display: flex; gap: 8px; }
.message-panel-toolbar :deep(.el-tabs__item.is-top) { height: 32px; padding: 0 12px; border-radius: 4px; color: #53627a; font-size: 12px; }
.message-panel-toolbar :deep(.el-tabs__item.is-active) { color: #3273ea; background: #edf4ff; font-weight: 600; }
.message-panel-tab { display: inline-flex; align-items: center; gap: 5px; }
.message-panel-count { min-width: 16px; height: 16px; padding: 0 4px; border-radius: 8px; background: #fff0f0; color: #d66f74; font-size: 10px; line-height: 16px; text-align: center; }
.message-panel-refresh { flex-shrink: 0; width: 28px; height: 28px; color: #718096; }
.message-panel-list { display: flex; flex-direction: column; min-height: 40px; max-height: calc(100dvh - 170px); overflow-y: auto; }
.message-panel-list[aria-busy="true"] { min-height: 80px; }
.message-panel-item { display: flex; flex-direction: column; gap: 8px; flex-shrink: 0; width: 100%; background: #fff; border: 0; border-bottom: 1px solid #edf1f5; padding: 16px 2px; text-align: left; cursor: pointer; }
.message-panel-item:hover { background: #f7faff; }
.message-panel-item-title { display: flex; align-items: center; gap: 10px; color: #42516a; font-size: 13px; }
.message-panel-item-title strong { flex: 1; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.message-panel-preview { color: #7789a5; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.7; font-size: 12px; }
.message-panel-item time { font-size: 11px; color: #a0adbd; }
.message-panel-dot { width: 6px; height: 6px; background: #3273ea; border-radius: 50%; }
.message-panel-empty { margin: 0; padding: 16px 0; color: #718096; font-size: 12px; }
.message-panel-actions { display: flex; align-items: center; padding-top: 14px; }
.message-panel-actions .el-button { color: #53627a; font-size: 12px; }
.header-message-button:focus-visible, .message-panel-item:focus-visible { outline: 2px solid #3273ea; outline-offset: 2px; }
</style>
