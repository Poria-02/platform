<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 审计与记录</div><h1>敏感数据日志</h1><p>审阅敏感数据访问与操作记录。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input :disabled="!canRead" v-model="query.createName" placeholder="搜索操作人" clearable @keyup.enter="search" />
        <el-input :disabled="!canRead" v-model="query.createBy" placeholder="搜索操作人 ID" clearable @keyup.enter="search" />
        <el-button :disabled="!canRead" type="primary" plain @click="search">查询</el-button>
        <el-button :disabled="!canRead" @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">

        <el-button :disabled="!canRead" @click="load">刷新</el-button>

      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button :disabled="!canRead" link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="rows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="sensitiveInfo" label="敏感信息" :min-width="230" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.sensitiveInfo, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="createName" label="操作人" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createName, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="userType" label="用户类型" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.userType, { format: value => userTypes[value] || value || '—' }, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createTime, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button :disabled="!canDetail" link type="primary" @click="showDetail(scope.row)">详情</el-button>

          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="!canRead || (loading)" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>

  </div><DetailDialog ref="detailRef" :user-types="userTypes" />
</template>

<script setup>
import DetailDialog from './DetailDialog.vue'
import { onMounted, reactive, ref } from 'vue'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useResourcePermissions } from '@/core/permissions'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'

const { canRead, canDetail } = useResourcePermissions('sensitiveLog')

const nextListRequest = useRequestController()

const userTypes = { 1: '用户', 2: '患者', 3: '医护' }

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ createName: '', createBy: '' })

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
function resetSearch() { query.createName = ''; query.createBy = ''; return search() }

onMounted(load)

const detailRef = ref()
function showDetail(row) { if (canDetail.value) return detailRef.value?.open(row) }
</script>
