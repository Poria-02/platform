<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 系统管理</div><h1>令牌管理</h1><p>查看在线令牌并撤销指定会话。</p></div>
    </header>
    <div class="resource-toolbar">

      <div class="page-heading__actions">

        <el-button :disabled="!canRead" @click="load">刷新</el-button>

      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="rows" row-key="accessToken" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="username" label="用户" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.username, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="clientId" label="客户端" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.clientId, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="issuedAt" label="签发时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.issuedAt, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="expiresAt" label="过期时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.expiresAt, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="accessToken" label="令牌" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.accessToken, { format: value => value ? `${String(value).slice(0, 8)}…${String(value).slice(-6)}` : '—' }, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="showDetail(scope.row)">查看</el-button><el-button link type="danger" v-stable-loading="deletingToken === scope.row.accessToken" :loading="deletingToken === scope.row.accessToken" :disabled="!canRemove || (Boolean(deletingToken))" @click="removeToken(scope.row)">删除令牌</el-button>

          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="!canRead || (loading)" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>

  </div>

  <DetailDialog :detail="detail" @close="detail.open = false" />
</template>

<script setup>
import DetailDialog from './DetailDialog.vue'
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import { useSession } from '@/core/session'
import { readSession } from '@/core/token'
import { clearMenuRoutes } from '@/core/router'
import router from '@/core/router'
import * as api from './api'
import { useResourcePermissions } from '@/core/permissions'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'

const { canRead, canRemove } = useResourcePermissions('token')

const nextListRequest = useRequestController()

const detail = reactive({ open: false, row: {} })

const deletingToken = ref('')

const session = useSession()

function showDetail(row) {
  detail.row = row
  detail.open = true
}

async function removeToken(row) {
  if (!canRemove.value || deletingToken.value) return
  try {
    await ElMessageBox.confirm('确认删除该令牌并结束对应会话吗？', '删除令牌', { type: 'warning' })
  } catch { return }

  deletingToken.value = row.accessToken
  try {
    const removed = await api.remove(row.accessToken)
    if (removed !== true) {
      ElMessage.warning('令牌不存在或已失效')
      await load()
      return
    }
    ElMessage.success('令牌已删除')
    if (detail.row.accessToken === row.accessToken) detail.open = false
    if (readSession()?.accessToken === row.accessToken) {
      session.clearLocal()
      clearMenuRoutes()
      await router.replace('/login')
    } else {
      await load()
    }
  } catch {
    // HTTP 层显示删除错误。
  } finally {
    deletingToken.value = ''
  }
}

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ page, reload: load, isLoading: () => loading.value })

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
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.token-value { overflow-wrap: anywhere; user-select: text; }
</style>
