<template>
  <div class="app-container">
    <el-card>
      <template #header>{{ props.title }}</template>
      <el-form :inline="true" :model="query">
        <el-form-item v-for="f in props.queryFields" :key="f.prop" :label="f.label">
          <el-input v-model="query[f.prop]" @keyup.enter="search" />
        </el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
      </el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column type="index" width="55" />
        <el-table-column v-for="c in props.columns" :key="c.prop" :prop="c.prop" :label="c.label">
          <template #default="s">{{ display(s.row[c.prop]) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="scope"><el-button link type="primary" @click="openDetail(scope.row)">查看</el-button></template>
        </el-table-column>
      </el-table>
      <Pagination v-show="total>0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
    </el-card>
    <el-dialog v-model="detail.open" title="敏感日志详情" width="min(760px, calc(100vw - 32px))" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item v-for="item in detail.items" :key="item.label" :label="item.label">{{ display(item.value) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>
<script setup>
import { pageApi } from './api'

const props = { title: '敏感数据日志', columns: [{ prop: 'title', label: '标题' }, { prop: 'createBy', label: '操作人' }, { prop: 'requestUri', label: '请求地址' }, { prop: 'createTime', label: '创建时间' }], queryFields: [{ prop: 'title', label: '标题' }, { prop: 'createBy', label: '操作人' }] }
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ title: '', createBy: '' })
const page = reactive({ current: 1, size: 10 })
const detail = reactive({ open: false, items: [] })
const display = value => value === null || value === undefined || value === '' ? '-' : value

async function load() {
  loading.value = true
  try {
    const result = (await pageApi.sensitiveLogs({ ...query, current: page.current, size: page.size })).data
    rows.value = result.records || []
    total.value = Number(result.total || 0)
  } catch (_) {
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
function search() { page.current = 1; return load() }
async function openDetail(row) {
  if (row.id === undefined || row.id === null) return
  const result = await pageApi.sensitiveLog(row.id)
  detail.items = Object.entries(result.data || row).map(([label, value]) => ({ label, value }))
  detail.open = true
}
onMounted(load)
</script>
