<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>动态路由</h1><p>维护网关路由并按需刷新生效。</p></div>
    </header>
    <div class="resource-toolbar">

      <div class="page-heading__actions">
        <el-button :disabled="!canRefresh" v-stable-loading="refreshing" :loading="refreshing" @click="refresh()">刷新网关路由</el-button>
        <el-button :disabled="!canRead" @click="load">刷新</el-button>
        <el-button :disabled="!canCreate || refreshing" type="primary" @click="openCreate()">新增路由</el-button>
      </div>
    </div>
    <p v-if="needsRefresh" class="route-refresh-hint" role="status">路由配置已变更，请手动点击“刷新网关路由”使配置生效。</p>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="routeId" label="路由 ID" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.routeId, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="routeName" label="名称" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.routeName, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="uri" label="目标 URI" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.uri, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="predicates" label="匹配规则" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.predicates, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="order" label="顺序" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.order, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">

            <el-button :disabled="!canEdit || refreshing || editConfirming" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button :disabled="!canRemove || refreshing" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" @saved="handleSaved" />
  </div>
</template>

<script setup>
import EditDialog from './EditDialog.vue'
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useResourcePermissions } from '@/core/permissions'

const nextListRequest = useRequestController()

const { canRead, canCreate, canEdit, canRemove, canRefresh } = useResourcePermissions('route')

const refreshing = ref(false)
const editConfirming = ref(false)
const needsRefresh = ref(false)

async function refresh() {
  if (!canRefresh.value || refreshing.value) return
  refreshing.value = true
  try {
    const result = await api.refresh()
    if (result === false) { ElMessage.error('网关路由刷新未完成，请重试'); return }
    needsRefresh.value = false
    ElMessage.success('网关路由已刷新')
    await load()
  }
  catch { /* HTTP 层显示错误 */ }
  finally { refreshing.value = false }
}

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  if (!canRead.value) { loading.value = false; loadError.value = ''; return }
  loading.value = true
  try {
    const params = {  }
    const data = await api.list(params, request)
    if (sequence !== loadSequence) return
    rows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []

    loadError.value = ''
  } catch (error) {
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

async function remove(row) {
  if (!canRemove.value || refreshing.value) return
  try { await ElMessageBox.confirm("确认删除这条路由吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    needsRefresh.value = true
    ElMessage.success('删除成功，请手动刷新网关路由后生效')

    await load()

  } catch { /* 请求层显示错误。 */ }
}

onMounted(load)

function handleSaved() {
  needsRefresh.value = true
  void load()
}

const editorRef = ref()
function openCreate(defaults = {}) {
  if (canCreate.value && !refreshing.value) editorRef.value?.openCreate(defaults)
}
async function openEdit(row) {
  if (!canEdit.value || refreshing.value || editConfirming.value) return
  editConfirming.value = true
  try {
    try {
      await ElMessageBox.confirm('修改网关路由属于专业运维操作，配置错误可能影响服务访问。请确认目标地址、匹配规则和过滤器配置，保存后需手动刷新网关路由生效。是否继续编辑？', '修改路由提醒', { type: 'warning', confirmButtonText: '继续编辑', cancelButtonText: '取消' })
    } catch { return }
    if (canEdit.value && !refreshing.value) await editorRef.value?.openEdit(row)
  } finally { editConfirming.value = false }
}
</script>

<style scoped>
.route-refresh-hint { margin: 0 0 16px; padding: 12px 16px; border: 1px solid #f0dfb5; border-radius: 4px; background: #fff9eb; color: #8a6525; font-size: 13px; line-height: 1.7; }
</style>
