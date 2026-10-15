<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 消息管理</div><h1>微信发送记录</h1><p>查询微信消息发送内容、接收账户和发送结果。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input :disabled="!canRead" v-model="query.mobile" placeholder="接收账户（精确匹配）" clearable @keyup.enter="search" />
        <el-input :disabled="!canRead" v-model="query.content" placeholder="搜索消息内容" clearable @keyup.enter="search" />
        <el-date-picker :disabled="!canRead" v-model="dateRange" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间" end-placeholder="结束时间" range-separator="至" unlink-panels />
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions"><el-button :disabled="!canRead" @click="load">刷新</el-button></div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="accountId" label="接收账户" min-width="180" show-overflow-tooltip />
        <el-table-column prop="templateKey" label="模板 Key" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="发送内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="status" label="发送状态" min-width="130"><template #default="scope">{{ displayResourceValue(scope.row.status, {}, sendStatuses) }}</template></el-table-column>
        <el-table-column prop="result" label="发送结果" min-width="220" show-overflow-tooltip />
        <el-table-column prop="msgId" label="微信消息 ID" min-width="180" show-overflow-tooltip />
        <el-table-column prop="orgId" label="机构 ID" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发送时间" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="90" fixed="right"><template #default="scope"><el-button :disabled="!canRead" link type="primary" @click="showDetail(scope.row)">详情</el-button></template></el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer"><span>共 {{ total }} 条记录</span><el-pagination :current-page="page.current" :page-size="page.size" :disabled="!canRead || (loading)" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" /></div>
    </section>
    <DetailDialog :detail="detail" kind="wx" @close="detail.open = false" />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { displayResourceValue } from '@/core/resourceValues'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'
import { logQueryError, sendStatuses } from '../options'
import DetailDialog from '../DetailDialog.vue'
import * as api from './api'

const { canRead } = useResourcePermissions('messageWxLog')
const nextListRequest = useRequestController()
const loading = ref(false)
const loadError = ref('')
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const query = reactive({ mobile: '', content: '', beginDate: '', endDate: '' })
const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })
const dateRange = computed({
  get: () => query.beginDate && query.endDate ? [query.beginDate, query.endDate] : null,
  set: value => { query.beginDate = value?.[0] || ''; query.endDate = value?.[1] || '' }
})
const detail = reactive({ open: false, row: {} })
let loadSequence = 0

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  if (!canRead.value) { loading.value = false; loadError.value = ''; return }
  const queryError = logQueryError(query)
  if (queryError) { loading.value = false; loadError.value = queryError; return }
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
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '消息服务暂不可用，请稍后重试' : error?.response?.data?.msg || error.message || '微信记录加载失败'
  } finally { if (sequence === loadSequence) loading.value = false }
}
function search() { page.current = 1; return load() }
function resetSearch() { Object.assign(query, { mobile: '', content: '', beginDate: '', endDate: '' }); return search() }
function showDetail(row) { if (canRead.value) { detail.row = { ...row }; detail.open = true } }

onMounted(load)
onBeforeUnmount(() => { loadSequence++ })
</script>

<style scoped>
.filter-bar :deep(.el-date-editor) { width: 380px; max-width: 100%; flex: 0 1 380px; }
</style>
