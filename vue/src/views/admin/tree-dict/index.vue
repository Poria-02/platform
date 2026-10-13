<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>分级字典</h1><p>维护层级字典与字典项。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input v-model="query.code" placeholder="搜索编码" clearable @keyup.enter="search" />
        <el-input v-model="query.name" placeholder="搜索名称" clearable @keyup.enter="search" />
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">
        <input ref="fileInput" type="file" accept=".treedict" hidden :disabled="transferBusy" @change="importFile">
        <el-button v-if="canImport" v-stable-loading="importing" :loading="importing" :disabled="transferBusy" @click="fileInput.click()">导入字典</el-button>
        <el-button v-if="canExport" v-stable-loading="exporting" :loading="exporting" :disabled="transferBusy" @click="exportFile()">导出全部</el-button>
        <el-button @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增字典</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="code" label="编码" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.code, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="name" label="名称" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.remark, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createTime, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button v-if="canItems" link type="primary" @click="selectDict(scope.row)">字典项</el-button><el-button v-if="canExport" link type="primary" @click="exportFile([scope.row.code])">导出</el-button>
            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="loading" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" :is-tree-choices="isTreeChoices" @saved="load" />
  </div>
  <ItemsDialog v-if="selected" :selected="selected" @close="selected = null" />
</template>

<script setup>
import ItemsDialog from './ItemsDialog.vue'
import EditDialog from './EditDialog.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'
import { saveDictionary } from '@/core/dictionaryTransfer'

const nextListRequest = useRequestController()

const { canCreate, canEdit, canRemove, canItems, canImport, canExport } = useResourcePermissions('treeDict')

const selected = ref(null)

function selectDict(row) { if (canItems.value) selected.value = row }

function refreshImported() { selected.value = null; load() }

const fileInput = ref()

const importing = ref(false)

const exporting = ref(false)

const transferBusy = computed(() => importing.value || exporting.value)

async function importFile(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file || transferBusy.value || !canImport.value) return
  if (!file.name.toLowerCase().endsWith('.treedict')) { ElMessage.error('请选择 .treedict 字典文件'); return }
  if (!file.size) { ElMessage.error('文件不能为空'); return }
  importing.value = true
  try {
    try { await ElMessageBox.confirm('导入会替换文件中同编码字典的内容，确认继续吗？', '导入字典', { type: 'warning' }) } catch { return }
    await api.importDict(file)
    ElMessage.success('字典导入成功')
    refreshImported()
  } catch (error) { ElMessage.error(error.message || '字典导入失败') }
  finally { importing.value = false }
}

async function exportFile(codes = []) {
  if (transferBusy.value || !canExport.value) return
  exporting.value = true
  try { saveDictionary(await api.exportDict(codes), 'dictionary.treedict') }
  catch (error) { ElMessage.error(error.message || '字典导出失败') }
  finally { exporting.value = false }
}

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ code: '', name: '' })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })

const isTreeChoices = computed(() => [{ label: '是', value: true }, { label: '否', value: false }])

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
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
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { query.code = ''; query.name = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条字典吗？", '确认删除', { type: 'warning' }) } catch { return }
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

<style scoped>.item-location { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; color: #808080; font-size: 12px; }</style>
