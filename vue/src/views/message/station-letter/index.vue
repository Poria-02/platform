<template>
  <div class="message-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 消息中心</div><h1>站内信</h1><p>集中查看任务提醒和站内沟通，及时处理重要消息。</p></div>
      <div class="page-heading__actions"><el-button @click="load">刷新</el-button><el-button :disabled="!permissions.canCreate" type="primary" @click="publish">发布站内信</el-button></div>
    </header>
    <div v-if="error" class="resource-error" role="alert"><span>{{ error }}</span><el-button link @click="load">重试</el-button></div>
    <section class="message-layout">
      <aside class="message-list-pane">
        <div class="message-list-heading"><div><strong>站内信列表</strong><small>共 {{ total }} 封</small></div><el-button link :disabled="busy || loading" @click="readAll">全部已读</el-button></div>
        <div class="message-search"><el-input v-model="keyword" clearable :maxlength="20" placeholder="搜索标题或内容" @keyup.enter="search" @clear="search" /><el-button @click="search">搜索</el-button></div>
        <div class="message-filter-read"><el-checkbox v-model="onlyUnread" @change="search">只看未读</el-checkbox></div>
        <div v-loading="loading" class="message-list" :aria-busy="loading">
          <button v-for="item in rows" :key="item.id" type="button" class="message-list-item" :class="{ selected: selected?.id === item.id, unread: item.status === 0 }" @click="selectMessage(item.id)">
            <span class="message-item-icon"><MenuIcon name="message" /></span>
            <span class="message-item-copy"><span class="message-item-meta"><span>{{ taskTypeName(item.type) }}</span><time>{{ date(item.createTime, true) }}</time></span><strong>{{ item.title }}</strong><span class="message-preview">{{ item.content }}</span></span>
            <span v-if="item.status === 0" class="message-unread-dot" aria-label="未读" />
          </button>
          <div v-if="!loading && !rows.length" class="message-empty"><MenuIcon name="message" /><strong>暂无站内信</strong><span>{{ onlyUnread ? '当前没有未读消息' : '新的消息会显示在这里' }}</span></div>
        </div>
        <div v-if="total > 0" class="message-pagination"><el-pagination small :current-page="current" :page-size="size" :total="total" layout="prev, pager, next" :disabled="loading" @update:current-page="changePage" /></div>
      </aside>
      <article v-loading="detailLoading" class="message-detail">
        <template v-if="selected">
          <div class="message-detail-heading"><span class="message-detail-category">站内信</span><h2>{{ selected.title }}</h2><div class="message-detail-meta"><span>{{ taskTypeName(selected.type) }}</span><time>{{ date(selected.createTime) }}</time><el-tag size="small" :type="selected.status === 0 ? 'warning' : 'info'">{{ selected.status === 0 ? '未读' : '已读' }}</el-tag></div></div>
          <div class="message-detail-actions"><el-button v-if="selected.status === 0" size="small" :disabled="busy" @click="markRead">标记已读</el-button></div>
          <div class="message-body">{{ selected.content }}</div>
        </template>
        <div v-else class="message-detail-empty"><MenuIcon name="message" /><strong>选择一封站内信</strong><span>消息正文会显示在这里。</span></div>
      </article>
    </section>
    <el-dialog v-model="editorOpen" title="发布站内信" width="min(680px, 94vw)" destroy-on-close :close-on-click-modal="false" :before-close="closeEditor">
      <el-form ref="editorForm" :model="draft" :rules="rules" label-position="top">
        <el-form-item label="接收人" prop="userIds"><MessageRecipients v-model="draft.userIds" :disabled="saving" @loading="recipientsLoading = $event" /></el-form-item>
        <el-form-item label="站内信通用模板" prop="templateKey"><el-select v-model="draft.templateKey" filterable remote clearable :remote-method="loadTemplates" :loading="templatesLoading" placeholder="搜索模板名称或key" style="width:100%" @change="selectTemplate"><el-option v-for="template in templates" :key="template.templateKey" :label="(template.title || '站内信模板') + '（' + template.templateKey + '）'" :value="template.templateKey" /></el-select></el-form-item>
        <p class="message-editor-hint">请先在通用模板管理配置并启用类型为“站内信”的模板。</p>
        <div v-if="templateError" class="resource-error" role="alert">{{ templateError }}<el-button v-if="!draft.templateKey" link @click="loadTemplates()">重试</el-button></div>
        <el-form-item v-for="field in templateFields" :key="field" :label="field" :prop="['param', field]" :rules="[{ required: true, whitespace: true, message: '请填写 ' + field, trigger: 'blur' }]"><el-input v-model="draft.param[field]" :placeholder="'填写模板参数 ' + field" /></el-form-item>
        <div v-if="selectedTemplate" class="message-template-preview"><span>内容预览</span><strong>{{ templatePreview.title }}</strong><p>{{ templatePreview.content }}</p></div>
      </el-form>
      <template #footer><el-button :disabled="saving || recipientsLoading" @click="editorOpen = false">取消</el-button><el-button type="primary" :loading="saving" :disabled="!permissions.canCreate || (recipientsLoading)" @click="save">发布</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MenuIcon from '@/ui/MenuIcon.vue'
import MessageRecipients from '@/components/MessageRecipients.vue'
import { useMessageCenter } from '@/core/messageCenter'
import { fetchDictionary } from '@/core/dictionaries'
import { displayResourceValue } from '@/core/resourceValues'
import { list as templateList } from '@/views/message/template/api'
import * as api from './api'

const center = useMessageCenter()
const permissions = computed(() => center.stationPermissions)
const route = useRoute()
const router = useRouter()
const keyword = ref('')
const onlyUnread = ref(false)
const current = ref(1)
const size = 10
const rows = ref([])
const taskTypes = ref([])
const total = ref(0)
const selected = ref(null)
const loading = ref(false)
const detailLoading = ref(false)
const error = ref('')
const busy = ref(false)
let listSequence = 0
let detailSequence = 0

function taskTypeName(type) { return displayResourceValue(type, {}, taskTypes.value) }
async function loadTaskTypes() {
  try { taskTypes.value = await fetchDictionary('pending_task_type') }
  catch (cause) { ElMessage.error(cause?.response?.data?.msg || cause.message || '待办类型加载失败') }
}

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
    const page = await api.list({ current: current.value, size, keyword: keyword.value.trim() || undefined, status: onlyUnread.value ? 0 : undefined })
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
function selectMessage(id) {
  if (String(route.query.id || '') === String(id)) void openMessage(id)
  else void router.replace({ query: { ...route.query, id } })
}
async function openMessage(id) {
  const sequence = ++detailSequence
  selected.value = null
  detailLoading.value = true
  error.value = ''
  try {
    const message = await api.detail(id)
    if (sequence !== detailSequence) return
    if (!message || Number(message.type) !== 99) throw new Error('站内信不存在')
    selected.value = message
    if (message.status === 0) { await api.read(id); message.status = 1 }
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

const editorOpen = ref(false)
const editorForm = ref(null)
const saving = ref(false)
const recipientsLoading = ref(false)
const draft = reactive({ userIds: [], templateKey: '', param: Object.create(null) })
const rules = {
  userIds: [{ type: 'array', required: true, min: 1, message: '请选择至少一位接收人', trigger: 'change' }],
  templateKey: [{ required: true, message: '请选择站内信模板', trigger: 'change' }]
}
const templates = ref([])
const templatesLoading = ref(false)
const selectedTemplate = ref(null)
const templateFields = ref([])
const templateError = ref('')
let templateSequence = 0
const templatePreview = computed(() => ({
  title: String(selectedTemplate.value?.title || '').replace(/\$\{(.+?)\}/g, (_, key) => draft.param[key] || ''),
  content: String(selectedTemplate.value?.content || '').replace(/\$\{(.+?)\}/g, (_, key) => draft.param[key] || '')
}))
async function loadTemplates(keywords = '') {
  const sequence = ++templateSequence
  templatesLoading.value = true
  templateError.value = ''
  try {
    const page = await templateList({ current: 1, size: 20, keywords, type: '2', status: '1' })
    if (sequence !== templateSequence) return
    templates.value = page?.records || []
    if (!templates.value.length) templateError.value = '没有匹配的已启用站内信模板，请先在通用模板管理配置。'
  } catch (cause) { if (sequence === templateSequence) templateError.value = cause.message || '模板加载失败' }
  finally { if (sequence === templateSequence) templatesLoading.value = false }
}
function selectTemplate(key) {
  draft.param = Object.create(null)
  templateFields.value = []
  selectedTemplate.value = null
  templateError.value = ''
  if (!key) return
  try {
    const template = templates.value.find(item => item.templateKey === key)
    const content = JSON.parse(template.content)
    const fields = new Set()
    for (const value of [content.title, content.content, content.params]) {
      for (const match of String(value || '').matchAll(/\$\{(.+?)\}/g)) fields.add(match[1])
    }
    templateFields.value = [...fields]
    for (const field of fields) draft.param[field] = ''
    selectedTemplate.value = content
  } catch { templateError.value = '模板内容格式有误，请在通用模板管理调整。' }
}
function publish() {
  if (!permissions.value.canCreate) return
  draft.userIds = []
  draft.templateKey = ''
  recipientsLoading.value = false
  selectTemplate('')
  void loadTemplates()
  editorOpen.value = true
}
function closeEditor(done) { if (!saving.value && !recipientsLoading.value) done() }
async function save() {
  if (!permissions.value.canCreate || !selectedTemplate.value || saving.value || recipientsLoading.value || !await editorForm.value.validate().catch(() => false)) return
  saving.value = true
  try {
    const result = await api.create({ userIds: [...draft.userIds], templateKey: draft.templateKey, param: { ...draft.param } })
    if (!result?.length) throw new Error('模板已停用，消息未发送')
    editorOpen.value = false
    ElMessage.success('站内信已发布')
    current.value = 1
    onlyUnread.value = false
    clearSelection()
    await load()
    void center.refresh()
  } catch (cause) { ElMessage.error(cause.message || '发布失败') }
  finally { saving.value = false }
}
watch(() => route.query.id, id => { if (id) void openMessage(String(id)) })
onMounted(async () => { void loadTaskTypes(); await load(); if (route.query.id) await openMessage(String(route.query.id)); void center.refresh() })
onBeforeUnmount(() => { listSequence++; detailSequence++; templateSequence++ })
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
