<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 平台配置</div><h1>公共参数</h1><p>维护服务使用的公共配置参数。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input :disabled="!canRead" v-model="query.publicKey" placeholder="搜索参数键" clearable @keyup.enter="search" />
        <el-input :disabled="!canRead" v-model="query.publicName" placeholder="搜索名称" clearable @keyup.enter="search" />
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">

        <el-button :disabled="!canRead" @click="load">刷新</el-button>
        <el-button :disabled="!canCreate" type="primary" @click="openCreate()">新增参数</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <div v-if="dictionaryError" class="resource-error" role="alert"><span>{{ dictionaryError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="publicId" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="publicKey" label="参数键" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.publicKey, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="publicValue" label="参数值" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.publicValue, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="publicName" label="名称" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.publicName, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="publicType" label="参数类型" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.publicType, { dictionary: 'param_type' }, publicTypeChoices, scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.status, {}, statusChoices, scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.remarks, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createTime, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">

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
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" :public-type-choices="publicTypeChoices" :status-choices="statusChoices" @saved="load" />
  </div>
</template>

<script setup>
import EditDialog from './EditDialog.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue, optionsForField } from '@/core/resourceValues'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'
import { fetchDictionary } from '@/core/dictionaries'

const nextListRequest = useRequestController()

const { canRead, canCreate, canEdit, canRemove } = useResourcePermissions('param')

const statusOptions = [{ label: '正常', value: '1' }, { label: '冻结', value: '2' }]

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ publicKey: '', publicName: '' })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })

const dictionaryItems = ref([])
const dictionaryError = ref('')

const publicTypeChoices = computed(() => optionsForField({ dictionary: 'param_type' }, dictionaryItems.value))

const statusChoices = computed(() => statusOptions)

let dictionaryLoadSequence = 0
async function loadDictionaries() {
  const sequence = ++dictionaryLoadSequence
  const types = ["param_type"]
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
  if (!canRead.value) { loading.value = false; loadError.value = ''; return }
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
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = (error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { query.publicKey = ''; query.publicName = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条参数吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.publicId)
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
