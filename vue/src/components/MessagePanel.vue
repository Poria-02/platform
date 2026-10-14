<template>
  <div class="header-messages">
    <el-badge :value="center.unread" :max="99" :hidden="!center.unread"><button class="header-message-button" type="button" aria-label="查看通知与公告" title="通知与公告" @click="open = true"><MenuIcon name="bell" /><span>消息</span></button></el-badge>
    <el-drawer v-model="open" title="通知与公告" size="min(460px, 100vw)" append-to-body @opened="refresh">
      <div class="message-panel-toolbar"><el-tabs v-model="active" @tab-change="load"><el-tab-pane :label="`公告${center.announcementUnread ? ` · ${center.announcementUnread}` : ''}`" name="announcement" /><el-tab-pane :label="`站内信${center.stationUnread ? ` · ${center.stationUnread}` : ''}`" name="station" /></el-tabs><el-button link :loading="loading" @click="refresh">刷新</el-button></div>
      <div v-if="error" class="resource-error" role="alert"><span>{{ error }}</span><el-button link @click="load">重试</el-button></div>
      <div v-loading="loading" class="message-panel-list">
        <button v-for="item in rows" :key="item.id" class="message-panel-item" type="button" @click="show(item.id)"><span class="message-panel-item-title"><strong>{{ item.title }}</strong><span v-if="isUnread(item)" class="message-panel-dot" /></span><time>{{ date(item.createTime) }}</time><span class="message-panel-preview">{{ item.content }}</span></button>
        <el-empty v-if="!loading && !rows.length" :description="active === 'announcement' ? '暂无公告' : '暂无站内信'" :image-size="60" />
      </div>
      <template #footer><el-button link type="primary" @click="show()">进入{{ active === 'announcement' ? '公示公告' : '站内信' }}</el-button></template>
    </el-drawer>
  </div>
</template>
<script setup>
import { onBeforeUnmount, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
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
  } catch (cause) { if (current === sequence) error.value = cause?.response?.status === 503 ? '消息服务暂不可用' : cause.message || '消息加载失败' }
  finally { if (current === sequence) loading.value = false }
}
function refresh() { void center.refresh(); void load() }
function show(id) { open.value = false; router.push({ path: active.value === 'announcement' ? '/message/announcement/index' : '/message/station-letter/index', query: id ? { id } : {} }) }
watch(() => session.current?.idleId, () => { sequence++; open.value = false; rows.value = []; error.value = ''; center.reset(); if (session.authenticated) void center.refresh() }, { immediate: true })
onBeforeUnmount(() => { sequence++ })
</script>
<style scoped>
.header-messages { margin-left: auto; }
.header-message-button { display: flex; align-items: center; gap: 7px; height: 34px; padding: 0 12px; border: 1px solid #dfe7f1; border-radius: 6px; background: #fff; color: #527092; cursor: pointer; }
.header-message-button:hover { color: #3273ea; background: #f3f7ff; }
.header-message-button svg { width: 17px; height: 17px; }
.message-panel-toolbar { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.message-panel-toolbar .el-tabs { flex: 1; min-width: 0; }
.message-panel-list { display: flex; flex-direction: column; gap: 10px; min-height: 180px; padding-top: 4px; }
.message-panel-item { display: flex; flex-direction: column; gap: 10px; width: 100%; background: #fff; border: 1px solid #dfe7f1; border-radius: 8px; padding: 16px 14px; text-align: left; cursor: pointer; }
.message-panel-item:hover { background: #f7faff; border-color: #b9cef0; }
.message-panel-item-title { display: flex; align-items: center; gap: 10px; color: #42516a; font-size: 13px; }
.message-panel-item-title strong { flex: 1; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.message-panel-preview { color: #7789a5; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.7; font-size: 12px; }
.message-panel-item time { font-size: 11px; color: #a0adbd; }
.message-panel-dot { width: 6px; height: 6px; background: #3273ea; border-radius: 50%; }
.header-message-button:focus-visible, .message-panel-item:focus-visible { outline: 2px solid #3273ea; outline-offset: 2px; }
</style>
