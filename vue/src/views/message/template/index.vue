<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 消息管理</div><h1>模板管理</h1><p>维护消息模板、发送配置和启用状态。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input :disabled="!canRead" v-model="query.keywords" placeholder="搜索标题、内容或模板 Key" clearable @keyup.enter="search" />
        <el-input :disabled="!canRead" v-model="query.templateKey" placeholder="模板 Key（精确匹配）" clearable @keyup.enter="search" />
        <el-select :disabled="!canRead" v-model="query.type" placeholder="全部模板类型" clearable><el-option v-for="option in templateTypes" :key="option.value" :label="option.label" :value="option.value" /></el-select>
        <el-select :disabled="!canRead" v-model="query.status" placeholder="全部状态" clearable><el-option v-for="option in templateStatuses" :key="option.value" :label="option.label" :value="option.value" /></el-select>
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">
        <el-button :disabled="!canRead" @click="load">刷新</el-button>
        <el-button type="primary" :disabled="!canCreate || (openingId !== null || busyId !== null)" @click="editorRef?.openCreate()">新增模板</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="title" label="模板标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="templateKey" label="模板 Key" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" min-width="140"><template #default="scope">{{ displayResourceValue(scope.row.type, {}, templateTypes) }}</template></el-table-column>
        <el-table-column prop="content" label="模板内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" min-width="100"><template #default="scope">{{ displayResourceValue(scope.row.status, {}, templateStatuses) }}</template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="scope">
            <el-button link type="primary" :loading="openingId === scope.row.id" :disabled="!canDetail || (openingId !== null || busyId !== null)" @click="openTemplate(scope.row, false)">详情</el-button>
            <el-button link type="primary" :disabled="!canEdit || (openingId !== null || busyId !== null)" @click="openTemplate(scope.row, true)">编辑</el-button>
            <el-button v-if="[1, 2].includes(Number(scope.row.status))" link type="primary" :loading="busyId === scope.row.id" :disabled="!canStatus || (openingId !== null || busyId !== null)" @click="changeStatus(scope.row)">{{ Number(scope.row.status) === 1 ? '停用' : '启用' }}</el-button>
            <el-button link type="danger" :disabled="!canRemove || (openingId !== null || busyId !== null)" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="!canRead || (loading)" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" @saved="load" />
    <DetailDialog :detail="detail" @close="detail.open = false" />
  </div>
</template>

<script setup>
import { showRequestError } from '@/core/http'
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'
import EditDialog from './EditDialog.vue'
import DetailDialog from './DetailDialog.vue'
import { templateStatuses, templateTypes } from './options'
import * as api from './api'

const { canRead, canDetail, canCreate, canEdit, canRemove, canStatus } = useResourcePermissions('messageTemplate')
const nextListRequest = useRequestController()
const nextDetailRequest = useRequestController()
const loading = ref(false)
const loadError = ref('')
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const query = reactive({ keywords: '', templateKey: '', type: '', status: '' })
const { syncListRoute, changePage, changePageSize } = useListRouteState({
  query, page, reload: load, isLoading: () => loading.value,
  allowedValues: { type: ['', '1', '2', '3', '4'], status: ['', '1', '2'] }
})
const editorRef = ref()
const detail = reactive({ open: false, row: {} })
const openingId = ref(null)
const busyId = ref(null)
let loadSequence = 0
let disposed = false

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  if (!canRead.value) { loading.value = false; loadError.value = ''; return }
  loading.value = true
  loadError.value = ''
  try {
    const params = await syncListRoute()
    if (!params || sequence !== loadSequence) return
    const data = await api.list(params, request)
    if (sequence !== loadSequence) return
    rows.value = data?.records || []
    total.value = Number(data?.total) || 0
    const lastPage = Math.max(1, Math.ceil(total.value / page.size))
    if (page.current > lastPage) { page.current = lastPage; await load() }
  } catch (error) {
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error.message || '模板加载失败'
  } finally { if (sequence === loadSequence) loading.value = false }
}

function search() { page.current = 1; return load() }
function resetSearch() { Object.assign(query, { keywords: '', templateKey: '', type: '', status: '' }); return search() }

async function openTemplate(row, editing) {
  if (!(editing ? canEdit.value : canDetail.value) || openingId.value !== null || busyId.value !== null) return
  openingId.value = row.id
  try {
    const data = await api.detail(row.id, nextDetailRequest())
    if (disposed) return
    if (!data?.id) throw new Error('模板不存在')
    if (editing) editorRef.value?.openEdit(data)
    else { detail.row = data; detail.open = true }
  } catch (error) {
    if (!disposed && error?.code !== 'ERR_CANCELED') showRequestError(error, '模板详情加载失败')
  } finally { if (!disposed) openingId.value = null }
}

async function changeStatus(row) {
  if (!canStatus.value || busyId.value !== null) return
  const status = Number(row.status) === 1 ? 2 : 1
  busyId.value = row.id
  try {
    const result = await api.changeStatus(row.id, status)
    if (result === false) { ElMessage.error('状态修改未完成'); return }
    ElMessage.success(status === 1 ? '模板已启用' : '模板已停用')
    await load()
  } catch (error) { if (!disposed) showRequestError(error, '状态修改失败') }
  finally { if (!disposed) busyId.value = null }
}

async function remove(row) {
  if (!canRemove.value || busyId.value !== null) return
  busyId.value = row.id
  try {
    try { await ElMessageBox.confirm('确认删除模板“' + row.title + '”吗？', '确认删除', { type: 'warning' }) }
    catch { return }
    if (disposed || !canRemove.value) return
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')
    if (rows.value.length === 1 && page.current > 1) page.current--
    await load()
  } catch (error) { if (!disposed) showRequestError(error, '删除失败') }
  finally { if (!disposed) busyId.value = null }
}

onMounted(load)
onBeforeUnmount(() => { disposed = true; loadSequence++ })
</script>
