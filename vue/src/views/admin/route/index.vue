<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>动态路由</h1><p>维护网关路由并按需刷新生效。</p></div>
    </header>
    <div class="resource-toolbar">

      <div class="page-heading__actions">
        <el-button v-if="canRefresh" v-stable-loading="refreshing" :loading="refreshing" @click="refresh()">刷新网关路由</el-button>
        <el-button @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增路由</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>

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

            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" @saved="load" />
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

const { canCreate, canEdit, canRemove, canRefresh } = useResourcePermissions('route')

const refreshing = ref(false)

async function refresh() {
  if (!canRefresh.value || refreshing.value) return
  refreshing.value = true
  try { await api.refresh(); ElMessage.success('网关路由已刷新'); await load() }
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
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条路由吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')

    await load()

  } catch { /* 请求层显示错误。 */ }
}

onMounted(load)

const editorRef = ref()
function openCreate(defaults = {}) {
  if (canCreate.value) editorRef.value?.openCreate(defaults)
}
function openEdit(row) {
  if (canEdit.value) return editorRef.value?.openEdit(row)
}
</script>
