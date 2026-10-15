<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>终端管理</h1><p>管理接入平台的 OAuth 客户端。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input :disabled="!canRead" v-model="query.clientId" placeholder="搜索客户端 ID" clearable @keyup.enter="search" />
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">
        <el-button :disabled="!canClear" v-stable-loading="clearing" :loading="clearing" @click="clear()">清除客户端缓存</el-button>
        <el-button :disabled="!canRead" @click="load">刷新</el-button>
        <el-button :disabled="!canCreate" type="primary" @click="openCreate()">新增终端</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <div v-if="dictionaryError" class="resource-error" role="alert"><span>{{ dictionaryError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="clientId" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="clientId" label="客户端 ID" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.clientId, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="scope" label="授权范围" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.scope, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="authorizedGrantTypes" label="授权方式" :min-width="270" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.authorizedGrantTypes, { dictionary: 'grant_types', separator: ',' }, authorizedGrantTypesChoices, scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="accessTokenValidity" label="令牌有效期" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.accessTokenValidity, {}, [], scope.row) }}</template>
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
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" :authorized-grant-types-choices="authorizedGrantTypesChoices" @saved="load" />
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

const { canRead, canCreate, canEdit, canRemove, canClear } = useResourcePermissions('client')

const clearing = ref(false)

async function clear() {
  if (!canClear.value || clearing.value) return
  clearing.value = true
  try { await api.clearCache(); ElMessage.success('客户端缓存已清除'); await load() }
  catch { /* HTTP 层显示错误 */ }
  finally { clearing.value = false }
}

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ clientId: '' })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })

const dictionaryItems = ref([])
const dictionaryError = ref('')

const authorizedGrantTypesChoices = computed(() => optionsForField({ dictionary: 'grant_types' }, dictionaryItems.value))

let dictionaryLoadSequence = 0
async function loadDictionaries() {
  const sequence = ++dictionaryLoadSequence
  const types = ["grant_types"]
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
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { query.clientId = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条终端吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.clientId)
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
