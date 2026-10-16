<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 小程序管理</div><h1>微信</h1><p>统一维护微信公众号、微信小程序的 AppId 和应用配置。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-select v-model="query.type" :disabled="!canRead" placeholder="全部类别" clearable><el-option label="公众号" value="WX" /><el-option label="小程序" value="MINI" /></el-select>
        <el-input v-model="query.appId" :disabled="!canRead" placeholder="AppId（精确匹配）" clearable @keyup.enter="search" />
        <el-input v-model="query.keywords" :disabled="!canRead" placeholder="搜索账号名称" clearable @keyup.enter="search" />
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">
        <el-button :disabled="!canRead" @click="load">刷新</el-button>
        <el-button :disabled="!canCreate" type="primary" @click="editorRef?.openCreate()">新增微信账号</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无微信账号" style="width:100%">
        <el-table-column prop="remark" label="账号名称" min-width="180" show-overflow-tooltip><template #default="scope">{{ displayResourceValue(scope.row.remark) }}</template></el-table-column>
        <el-table-column label="类别" width="150"><template #default="scope">{{ scope.row.type === 'WX' ? '公众号' : '小程序' }}</template></el-table-column>
        <el-table-column prop="appId" label="AppId" min-width="220" show-overflow-tooltip />
        <el-table-column label="AppSecret" width="140"><template #default="scope">{{ scope.row.secretConfigured ? '已配置' : '未配置' }}</template></el-table-column>
        <el-table-column prop="redirectUrl" label="回调地址" min-width="220" show-overflow-tooltip><template #default="scope">{{ displayResourceValue(scope.row.redirectUrl) }}</template></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button :disabled="!canEdit" link type="primary" @click="editorRef?.openEdit(scope.row)">编辑</el-button>
            <el-button :disabled="!canRemove || removingId !== null" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="!canRead || loading" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" @saved="load" />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSession } from '@/core/session'
import { displayResourceValue } from '@/core/resourceValues'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import EditDialog from './EditDialog.vue'
import * as api from './api'

const session = useSession()
const canRead = computed(() => session.hasPermission('sys_social_details_page'))
const canCreate = computed(() => session.hasPermission('sys_social_details_add'))
const canEdit = computed(() => session.hasPermission('sys_social_details_edit'))
const canRemove = computed(() => session.hasPermission('sys_social_details_del'))
const nextListRequest = useRequestController()
const editorRef = ref()
const loading = ref(false)
const loadError = ref('')
const rows = ref([])
const total = ref(0)
const removingId = ref(null)
const page = reactive({ current: 1, size: 10 })
const query = reactive({ type: '', appId: '', keywords: '' })
const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value, allowedValues: { type: ['', 'WX', 'MINI'] } })
let loadSequence = 0

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
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.data?.msg || error.message || '微信账号加载失败'
  } finally { if (sequence === loadSequence) loading.value = false }
}

function search() { page.current = 1; return load() }
function resetSearch() { Object.assign(query, { type: '', appId: '', keywords: '' }); return search() }

async function remove(row) {
  if (!canRemove.value || removingId.value !== null) return
  try { await ElMessageBox.confirm('确认删除微信账号“' + (row.remark || row.appId) + '”吗？', '确认删除', { type: 'warning' }) } catch { return }
  if (removingId.value !== null) return
  removingId.value = row.id
  try {
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')
    await load()
  } catch (error) { ElMessage.error(error?.response?.data?.msg || error.message || '删除失败') }
  finally { removingId.value = null }
}

onMounted(load)
onBeforeUnmount(() => { loadSequence++ })
</script>
