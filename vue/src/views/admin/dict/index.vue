<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>字典管理</h1><p>维护字典类型与字典项。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input v-model="query.type" placeholder="搜索字典类型" clearable @keyup.enter="search" />
        <el-select v-model="query.system" placeholder="选择所属系统" clearable><el-option v-for="option in systemChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">
        <input ref="fileInput" type="file" accept=".dict" hidden :disabled="transferBusy" @change="importFile">
        <el-button v-if="canImport" v-stable-loading="importing" :loading="importing" :disabled="transferBusy" @click="fileInput.click()">导入字典</el-button>
        <el-button v-if="canExport" v-stable-loading="exporting" :loading="exporting" :disabled="transferBusy" @click="exportFile()">导出全部</el-button>
        <el-button @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增字典</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>
      <div v-if="dictionaryError" class="resource-error" role="alert"><span>{{ dictionaryError }}</span><el-button link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="type" label="字典类型" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.type, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="说明" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.description, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="system" label="所属系统" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.system, { dictionary: 'dict_type' }, systemChoices, scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.remarks, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button v-if="canItems" link type="primary" @click="selected = scope.row">字典项</el-button><el-button v-if="canExport" link type="primary" @click="exportFile([scope.row.type])">导出</el-button>
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
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" :system-choices="systemChoices" @saved="load" />
  </div>
  <ItemsDialog v-if="selected" :selected="selected" @close="selected = null" />
</template>

<script setup>
import ItemsDialog from './ItemsDialog.vue'
import EditDialog from './EditDialog.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue, optionsForField } from '@/core/resourceValues'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'
import { saveDictionary } from '@/core/dictionaryTransfer'
import { fetchDictionary } from '@/core/dictionaries'

const nextListRequest = useRequestController()

const { canCreate, canEdit, canRemove, canItems, canImport, canExport } = useResourcePermissions('dict')

const selected = ref(null)

function refreshImported() { selected.value = null; load() }

const fileInput = ref()

const importing = ref(false)

const exporting = ref(false)

const transferBusy = computed(() => importing.value || exporting.value)

async function importFile(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file || transferBusy.value || !canImport.value) return
  if (!file.name.toLowerCase().endsWith('.dict')) { ElMessage.error('请选择 .dict 字典文件'); return }
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
  try { saveDictionary(await api.exportDict(codes), 'dictionary.dict') }
  catch (error) { ElMessage.error(error.message || '字典导出失败') }
  finally { exporting.value = false }
}

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ type: '', system: '' })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })

const dictionaryItems = ref([])
const dictionaryError = ref('')

const systemChoices = computed(() => optionsForField({ dictionary: 'dict_type' }, dictionaryItems.value))

let dictionaryLoadSequence = 0
async function loadDictionaries() {
  const sequence = ++dictionaryLoadSequence
  const types = ["dict_type"]
  const results = await Promise.allSettled(types.map(async type => {
    const data = await fetchDictionary(type)
    if (!Array.isArray(data)) throw new Error('字典数据格式错误')
    return data.map(entry => ({ ...entry, type }))
  }))
  if (sequence !== dictionaryLoadSequence) return
  dictionaryItems.value = results.flatMap((result, i) => result.status === 'fulfilled'
    ? result.value : dictionaryItems.value.filter(entry => entry.type === types[i]))
  dictionaryError.value = results.some(result => result.status === 'rejected') ? '部分选项加载失败，请重试' : ''
}

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  loading.value = true
  try {
    const params = await syncListRoute()
    if (!params || sequence !== loadSequence) return
    void loadDictionaries()
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
function resetSearch() { query.type = ''; query.system = ''; return search() }

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
