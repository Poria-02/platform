<template>
  <div class="app-container">
    <el-card>
      <template #header>{{ props.title }}</template>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="warning" @click="refresh">刷新网关路由</el-button>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column v-for="c in props.columns" :key="c.prop" :prop="c.prop" :label="c.label">
          <template #default="s">{{ display(s.row[c.prop]) }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="s">
            <el-button link type="danger" @click="remove(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageApi } from './api'

const props = { title: '网关路由管理', columns: [{ prop: 'routeId', label: '路由 ID' }, { prop: 'routeName', label: '路由名称' }, { prop: 'uri', label: '目标地址' }, { prop: 'order', label: '顺序' }] }
const loading = ref(false)
const rows = ref([])
const display = value => value === null || value === undefined || value === '' ? '-' : value

async function load() {
  loading.value = true
  try {
    rows.value = (await pageApi.routes()).data || []
  } catch (_) {
    rows.value = []
  } finally {
    loading.value = false
  }
}
async function remove(row) {
  if (row.id === undefined) return ElMessage.warning('缺少路由标识')
  await ElMessageBox.confirm('确认删除该路由吗？', '系统提示', { type: 'warning' })
  await pageApi.deleteRoute(row.id)
  ElMessage.success('删除成功')
  await load()
}
async function refresh() {
  await pageApi.refreshRoutes()
  ElMessage.success('网关路由已刷新')
  await load()
}
onMounted(load)
</script>
