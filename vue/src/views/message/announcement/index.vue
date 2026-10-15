<template>
  <div class="message-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 消息中心</div><h1>公示公告</h1><p>集中查看公告通知，及时了解重要事项。</p></div>
      <div class="page-heading__actions"><el-button @click="load">刷新</el-button><el-button :disabled="!canManage" @click="toggleManagement">{{ managing ? '我的公告' : '公告管理' }}</el-button><el-button :disabled="!permissions.canCreate" type="primary" @click="edit()">新增公告</el-button></div>
    </header>
    <div v-if="error" class="resource-error" role="alert"><span>{{ error }}</span><el-button link @click="load">重试</el-button></div>
    <section class="message-layout message-layout--notice">
      <aside class="message-list-pane">
        <div class="message-list-heading"><div><strong>{{ managing ? '公告记录' : '我的公告' }}</strong><small>共 {{ total }} 条</small></div><el-button v-if="!managing" link :disabled="busy || loading" @click="readAll">全部已读</el-button></div>
        <div class="message-search"><el-input v-model="keyword" clearable :maxlength="managing ? 100 : 20" placeholder="搜索标题或内容" @keyup.enter="search" @clear="search" /><el-button @click="search">搜索</el-button></div>
        <div v-if="managing" class="message-filters"><el-select v-model="noticeStatus" @change="search"><el-option label="已发布" :value="1" /><el-option :disabled="!permissions.canViewDrafts" label="草稿" :value="0" /></el-select></div>
        <div v-else class="message-filter-read"><el-checkbox v-model="onlyUnread" @change="search">只看未读</el-checkbox></div>
        <div v-loading="loading" class="message-list" :aria-busy="loading">
          <button v-for="item in rows" :key="item.id" type="button" class="message-list-item" :class="{ selected: selected?.id === item.id, unread: !managing && item.status === 0 }" @click="selectMessage(item.id)">
            <span class="message-item-icon"><MenuIcon name="bell" /></span>
            <span class="message-item-copy"><span class="message-item-meta"><span>{{ managing ? (item.publisher || '系统公告') : '公示公告' }}</span><time>{{ date(item.publishTime || item.createTime, true) }}</time></span><strong>{{ item.title }}</strong><span class="message-preview">{{ item.content }}</span><span v-if="managing" class="message-state">{{ noticeState(item.status) }}</span></span>
            <span v-if="!managing && item.status === 0" class="message-unread-dot" aria-label="未读" />
          </button>
          <div v-if="!loading && !rows.length" class="message-empty"><MenuIcon name="bell" /><strong>暂无公告</strong><span>{{ !managing && onlyUnread ? '当前没有未读消息' : '新的消息会显示在这里' }}</span></div>
        </div>
        <div v-if="total > 0" class="message-pagination"><el-pagination small :current-page="current" :page-size="size" :total="total" layout="prev, pager, next" :disabled="loading" @update:current-page="changePage" /></div>
      </aside>
      <article v-loading="detailLoading" class="message-detail">
        <template v-if="selected">
          <div class="message-detail-heading"><span class="message-detail-category">公示公告</span><h2>{{ selected.title }}</h2><div class="message-detail-meta"><span>{{ managing ? (selected.publisher || '系统公告') : '公告通知' }}</span><time>{{ date(selected.publishTime || selected.createTime) }}</time><el-tag v-if="managing" size="small">{{ noticeState(selected.status) }}</el-tag><el-tag v-else size="small" :type="selected.status === 0 ? 'warning' : 'info'">{{ selected.status === 0 ? '未读' : '已读' }}</el-tag></div></div>
          <div class="message-detail-actions">
            <el-button v-if="!managing && selected.status === 0" size="small" :disabled="busy" @click="markRead">标记已读</el-button>
            <template v-if="managing"><el-button size="small" :disabled="!permissions.canEdit || (busy)" @click="edit(selected)">编辑</el-button><el-button size="small" :disabled="!permissions.canSend || (busy)" type="primary" plain @click="sendNotice">{{ selected.status === 1 ? '再次发布' : '发布公告' }}</el-button><el-button v-if="selected.status === 1" size="small" :disabled="!permissions.canEdit || (busy)" @click="withdrawNotice">撤回公告</el-button><el-button size="small" :disabled="!permissions.canDelete || (busy)" type="danger" plain @click="removeSelected">删除</el-button></template>
          </div>
          <div class="message-body">{{ selected.content }}</div>
        </template>
        <div v-else class="message-detail-empty"><MenuIcon name="bell" /><strong>选择一条公告</strong><span>公告正文会显示在这里。</span></div>
      </article>
    </section>
    <el-dialog v-model="editorOpen" :title="`${editingId ? '编辑' : '新增'}公告`" width="min(680px, 94vw)" destroy-on-close :close-on-click-modal="false" :before-close="closeEditor">
      <el-form ref="editorForm" :model="draft" :rules="rules" label-position="top">
        <el-form-item label="接收人" prop="userIds"><MessageRecipients v-model="draft.userIds" :disabled="!permissions.canSend || (saving)" @loading="recipientsLoading = $event" /><p class="message-editor-hint">保存草稿可不选接收人，发布时至少选择一人。</p></el-form-item>
        <el-form-item label="标题" prop="title"><el-input v-model="draft.title" maxlength="200" show-word-limit /></el-form-item>
        <el-form-item label="正文" prop="content"><el-input v-model="draft.content" type="textarea" :rows="12" maxlength="20000" show-word-limit /></el-form-item>
      </el-form>
      <p class="message-editor-hint">保存后可单独发布公告。每次发布生成新的通知，已发送通知保留原内容和阅读状态。</p>
      <template #footer><el-button :disabled="saving || recipientsLoading" @click="editorOpen = false">取消</el-button><el-button :loading="saving" :disabled="!(editingId ? permissions.canEdit : permissions.canCreate) || (recipientsLoading)" @click="save(false)">保存草稿</el-button><el-button type="primary" :loading="saving" :disabled="!(editingId ? permissions.canEdit : permissions.canCreate) || (!permissions.canSend || (recipientsLoading))" @click="save(true)">保存并发布</el-button></template>
    </el-dialog>
    <el-dialog v-model="sendDialog.open" title="发布公告" width="min(680px, 94vw)" destroy-on-close :close-on-click-modal="false" :before-close="closeSendDialog">
      <el-form ref="sendForm" :model="sendDialog" :rules="{ userIds: recipientRules }" label-position="top"><el-form-item label="接收人" prop="userIds"><MessageRecipients v-model="sendDialog.userIds" :disabled="busy" @loading="recipientsLoading = $event" /></el-form-item></el-form>
      <p v-if="error" class="resource-error" role="alert">{{ error }}</p>
      <template #footer><el-button :disabled="busy || recipientsLoading" @click="sendDialog.open = false">取消</el-button><el-button type="primary" :loading="busy" :disabled="!permissions.canSend || (recipientsLoading)" @click="confirmSendNotice">发布</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MenuIcon from '@/ui/MenuIcon.vue'
import MessageRecipients from '@/components/MessageRecipients.vue'
import { useMessageCenter } from '@/core/messageCenter'
import * as api from './api'

const center = useMessageCenter()
const permissions = computed(() => center.announcementPermissions)
const canManage = computed(() => permissions.value.canCreate || permissions.value.canEdit || permissions.value.canDelete || permissions.value.canSend || permissions.value.canViewDrafts)
const route = useRoute()
const router = useRouter()
const management = ref(false)
const managing = computed(() => management.value && canManage.value)
const keyword = ref('')
const onlyUnread = ref(false)
const noticeStatus = ref(1)
const current = ref(1)
const size = 10
const rows = ref([])
const total = ref(0)
const selected = ref(null)
const loading = ref(false)
const detailLoading = ref(false)
const error = ref('')
const busy = ref(false)
let listSequence = 0
let detailSequence = 0
const noticeState = status => ['草稿', '已发布', '已撤回'][status] || '未知状态'

function date(value, short = false) {
  if (!value) return '—'
  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) return String(value)
  return parsed.toLocaleString('zh-CN', short ? { month: '2-digit', day: '2-digit' } : { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
async function load() {
  const sequence = ++listSequence
  loading.value = true
  error.value = ''
  try {
    const params = { current: current.value, size, keyword: keyword.value.trim() || undefined }
    const page = await (managing.value
      ? api.recordList({ ...params, status: noticeStatus.value })
      : api.list({ ...params, status: onlyUnread.value ? 0 : undefined }))
    if (sequence !== listSequence) return
    rows.value = page?.records || []
    total.value = Number(page?.total) || 0
  } catch (cause) { if (sequence === listSequence) error.value = cause?.response?.status === 503 ? '消息服务暂不可用，请稍后重试' : cause.message || '消息加载失败' }
  finally { if (sequence === listSequence) loading.value = false }
}
function clearSelection() {
  selected.value = null
  detailSequence++
  if (route.query.id) { const query = { ...route.query }; delete query.id; void router.replace({ query }) }
}
function search() { current.value = 1; clearSelection(); void load() }
function changePage(value) { current.value = value; clearSelection(); void load() }
function toggleManagement() { management.value = !managing.value; onlyUnread.value = false; noticeStatus.value = 1; search() }
function selectMessage(id) {
  if (managing.value || String(route.query.id || '') === String(id)) void openMessage(id)
  else void router.replace({ query: { ...route.query, id } })
}
async function openMessage(id) {
  const sequence = ++detailSequence
  const record = managing.value
  selected.value = null
  detailLoading.value = true
  error.value = ''
  try {
    const message = await (record ? api.recordDetail(id) : api.detail(id))
    if (sequence !== detailSequence) return
    if (!message || (!record && Number(message.type) !== 98)) throw new Error('公告不存在')
    selected.value = message
    if (!record && message.status === 0) { await api.read(id); message.status = 1 }
    if (sequence !== detailSequence) return
    rows.value = rows.value.map(row => row.id === id ? { ...row, ...message } : row)
    void center.refresh()
  } catch (cause) { if (sequence === detailSequence) error.value = cause.message || '详情加载失败' }
  finally { if (sequence === detailSequence) detailLoading.value = false }
}
async function run(action) {
  if (busy.value) return
  busy.value = true
  error.value = ''
  try { await action(); await load(); void center.refresh() }
  catch (cause) { error.value = cause.message || '操作失败' }
  finally { busy.value = false }
}
function markRead() { const id = selected.value.id; return run(async () => { await api.read(id); if (selected.value?.id === id) selected.value.status = 1 }) }
function readAll() { return run(async () => { await api.readAll(); clearSelection() }) }
function sendNotice() {
  if (!permissions.value.canSend || !managing.value || busy.value) return
  sendDialog.id = selected.value.id
  sendDialog.userIds = []
  recipientsLoading.value = false
  error.value = ''
  sendDialog.open = true
}
async function confirmSendNotice() {
  if (!permissions.value.canSend || busy.value || recipientsLoading.value || !await sendForm.value.validate().catch(() => false)) return
  const id = sendDialog.id
  return run(async () => { await api.send({ id, userIds: [...sendDialog.userIds] }); noticeStatus.value = 1; if (selected.value?.id === id) selected.value = await api.recordDetail(id); sendDialog.open = false; ElMessage.success('公告已发布') })
}
function withdrawNotice() {
  if (!permissions.value.canEdit || !managing.value) return
  const id = selected.value.id
  return run(async () => { await api.status({ id, status: 2 }); if (selected.value?.id === id) { selected.value.status = 2; selected.value.publishTime = null } })
}
async function removeSelected() {
  if (!permissions.value.canDelete || !managing.value || busy.value) return
  const id = selected.value.id
  try { await ElMessageBox.confirm('删除后将无法恢复，确定删除公告吗？', '删除公告', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }) }
  catch { return }
  await run(async () => { await api.remove(id); clearSelection() })
}

const editorOpen = ref(false)
const editorForm = ref(null)
const editingId = ref('')
const saving = ref(false)
const recipientsLoading = ref(false)
const draft = reactive({ title: '', content: '', userIds: [] })
const sendDialog = reactive({ open: false, id: '', userIds: [] })
const sendForm = ref(null)
const recipientRules = [{ type: 'array', required: true, min: 1, message: '请选择至少一位接收人', trigger: 'change' }]
const rules = {
  title: [{ required: true, whitespace: true, message: '请填写标题', trigger: 'blur' }],
  content: [{ required: true, whitespace: true, message: '请填写正文', trigger: 'blur' }]
}
function edit(message) {
  if (message ? !permissions.value.canEdit : !permissions.value.canCreate) return
  editingId.value = message?.id || ''
  draft.title = message?.title || ''
  draft.content = message?.content || ''
  draft.userIds = []
  recipientsLoading.value = false
  editorOpen.value = true
}
function closeEditor(done) { if (!saving.value && !recipientsLoading.value) done() }
function closeSendDialog(done) { if (!busy.value && !recipientsLoading.value) done() }
async function save(sendNow) {
  if (editingId.value ? !permissions.value.canEdit : !permissions.value.canCreate) return
  if (sendNow && !permissions.value.canSend) return
  if (saving.value || recipientsLoading.value || !await editorForm.value.validate().catch(() => false)) return
  if (sendNow && !draft.userIds.length) { ElMessage.warning('请选择至少一位接收人'); return }
  saving.value = true
  try {
    const data = { title: draft.title.trim(), content: draft.content.trim() }
    if (editingId.value) await api.update({ ...data, id: editingId.value })
    else editingId.value = await api.create(data)
    if (sendNow) await api.send({ id: editingId.value, userIds: [...draft.userIds] })
    editorOpen.value = false
    ElMessage.success(sendNow ? '公告已发布' : '公告草稿已保存')
    management.value = true
    current.value = 1
    onlyUnread.value = false
    noticeStatus.value = !sendNow && permissions.value.canViewDrafts ? 0 : 1
    clearSelection()
    await load()
    await openMessage(editingId.value)
    void center.refresh()
  } catch (cause) { ElMessage.error(cause.message || '保存失败') }
  finally { saving.value = false }
}
watch(() => route.query.id, id => { if (id) { if (managing.value) { management.value = false; current.value = 1; void load() } void openMessage(String(id)) } })
onMounted(async () => { await load(); if (route.query.id) await openMessage(String(route.query.id)); void center.refresh() })
onBeforeUnmount(() => { listSequence++; detailSequence++ })
</script>

<style scoped>
.message-page { --message-accent: #3273ea; display: flex; flex: 1; flex-direction: column; min-height: 0; }
.message-page > .page-heading, .message-page > .resource-error { flex-shrink: 0; }
.message-layout { display: grid; flex: 1; grid-template-columns: minmax(260px, 350px) minmax(0, 1fr); min-height: 0; background: white; border: 1px solid #e4eaf1; border-radius: 8px; overflow: hidden; }
.message-layout--notice { grid-template-columns: minmax(260px, 330px) minmax(0, 1fr); }
.message-list-pane { min-width: 0; min-height: 0; border-right: 1px solid #e8edf3; display: flex; flex-direction: column; }
.message-list-heading, .message-search, .message-filters, .message-filter-read { flex-shrink: 0; }
.message-list-heading { display: flex; align-items: center; justify-content: space-between; padding: 16px; border-bottom: 1px solid #edf1f5; font-size: 13px; }
.message-list-heading small { display: block; margin-top: 6px; font-size: 11px; color: #8a97aa; }
.message-search { display: flex; gap: 6px; padding: 10px 12px; }
.message-search .el-button { margin: 0; }
.message-filters { padding: 0 12px; }
.message-filter-read { padding: 0 14px 6px; }
.message-list { flex: 1; min-height: 0; overflow-y: auto; }
.message-list-item { position: relative; display: flex; align-items: flex-start; gap: 10px; width: 100%; padding: 18px 14px; border: 0; border-bottom: 1px solid #edf1f5; background: white; text-align: left; cursor: pointer; }
.message-list-item:hover, .message-list-item.selected { background: #f2f7ff; }
.message-item-icon { display: grid; place-items: center; width: 28px; height: 28px; border-radius: 7px; background: #eef4ff; color: #6d91c4; flex-shrink: 0; }
.message-item-icon svg { width: 18px; height: 18px; }
.message-item-copy { min-width: 0; flex: 1; display: flex; flex-direction: column; gap: 7px; }
.message-item-meta { display: flex; justify-content: space-between; gap: 8px; color: #8795a9; font-size: 11px; }
.message-item-copy strong { color: #42516a; font-size: 13px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.message-list-item.unread strong { font-weight: 700; }
.message-preview { color: #94a0b2; font-size: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.message-unread-dot { width: 6px; height: 6px; background: var(--message-accent); border-radius: 50%; margin-top: 7px; flex-shrink: 0; }
.message-state { color: #7b8da4; font-size: 11px; }
.message-pagination { flex-shrink: 0; margin-top: auto; padding: 14px 8px; border-top: 1px solid #edf1f5; display: flex; justify-content: center; }
.message-detail { min-width: 0; min-height: 0; overflow-y: auto; padding: 32px; }
.message-detail-heading h2 { margin: 14px 0; font-size: 23px; font-weight: 600; line-height: 1.5; overflow-wrap: anywhere; }
.message-detail-category { color: var(--message-accent); font-size: 12px; }
.message-detail-meta { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; color: #8a97aa; font-size: 12px; }
.message-detail-actions { display: flex; flex-wrap: wrap; gap: 8px; padding: 18px 0; border-bottom: 1px solid #edf1f5; }
.message-detail-actions .el-button { margin: 0; }
.message-body { padding-top: 24px; white-space: pre-wrap; overflow-wrap: anywhere; line-height: 1.9; color: #46546a; font-size: 14px; }
.message-empty, .message-detail-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; color: #95a2b5; font-size: 12px; padding: 50px 15px; text-align: center; }
.message-empty svg, .message-detail-empty svg { width: 32px; height: 32px; }
.message-empty strong, .message-detail-empty strong { font-weight: 500; color: #6e7f98; }
.message-detail-empty { min-height: 100%; }
.message-editor-hint { color: #8a97aa; font-size: 12px; }
.message-template-preview { display: flex; flex-direction: column; gap: 10px; border: 1px solid #dfe7f1; border-radius: 8px; padding: 16px; margin-top: 16px; }
.message-template-preview span { color: #8a97aa; font-size: 12px; }
.message-template-preview strong { color: #42516a; font-size: 14px; overflow-wrap: anywhere; }
.message-template-preview p { margin: 0; color: #7789a5; font-size: 13px; line-height: 1.7; white-space: pre-wrap; overflow-wrap: anywhere; }
.message-list-item:focus-visible { outline: 2px solid var(--message-accent); outline-offset: -2px; }
@media (max-width: 1100px) { .message-layout, .message-layout--notice { grid-template-columns: 270px minmax(0, 1fr); } .message-detail { padding: 22px; } }
@media (max-width: 760px) { .message-layout, .message-layout--notice { display: flex; flex-direction: column; } .message-list-pane { flex-shrink: 0; border-right: 0; } .message-list { flex: auto; max-height: 330px; } .message-detail { flex: 1; overflow: visible; border-top: 1px solid #edf1f5; } .message-detail-empty { min-height: 170px; } }
</style>
