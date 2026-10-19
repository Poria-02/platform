<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>标签管理</h1><p>维护标签和标签项。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input :disabled="!canRead" v-model="query.tagKey" placeholder="搜索标签键" clearable @keyup.enter="search" />
        <el-input :disabled="!canRead" v-model="query.name" placeholder="搜索名称" clearable @keyup.enter="search" />
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">

        <el-button :disabled="!canRead" @click="load">刷新</el-button>
        <el-button :disabled="!canCreate" type="primary" @click="openCreate()">新增标签</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="tagKey" label="标签键" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.tagKey, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="name" label="名称" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createTime, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button :disabled="!canItems" link type="primary" @click="selected = scope.row">标签项</el-button>
            <el-button :disabled="!canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button :disabled="!canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="!canRead || (loading)" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" @saved="load" />
  </div>
  <ItemsDialog v-if="selected" :selected="selected" :status-options="statusOptions" @close="selected = null" />
</template>

<script setup>
import ItemsDialog from './ItemsDialog.vue'
import EditDialog from './EditDialog.vue'
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'

const nextListRequest = useRequestController()

const { canRead, canCreate, canEdit, canRemove, canItems } = useResourcePermissions('tag')

const selected = ref(null)

const statusOptions = [{ label: '启用', value: 1 }, { label: '停用', value: 2 }]

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ tagKey: '', name: '' })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  if (!canRead.value) { loading.value = false; loadError.value = ''; return }
  loading.value = true
  try {
    const params = await syncListRoute()
    if (!params || sequence !== loadSequence) return
    const data = await api.list(params, request)
    if (sequence !== loadSequence) return
    rows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []
    total.value = Number(Array.isArray(data) ? data.length : data?.total ?? data?.totalCount ?? rows.value.length)

    loadError.value = ''
  } catch (error) {
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = (error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { query.tagKey = ''; query.name = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条标签吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')
    if (rows.value.length === 1 && page.current > 1) page.current--
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
