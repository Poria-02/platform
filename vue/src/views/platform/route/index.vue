<template>
  <div class="app-container">
    <el-card>
      <template #header>{{ props.title }}</template>
      <div class="toolbar">
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="success" @click="openCreate">新增路由</el-button>
        <el-button type="warning" @click="refresh">刷新网关路由</el-button>
      </div>
      <div class="table-scroll"><el-table v-loading="loading" :data="rows" border class="compact-table">
        <el-table-column v-for="c in props.columns" :key="c.prop" :prop="c.prop" :label="c.label">
          <template #default="s">{{ display(s.row[c.prop]) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="openEdit(s.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table></div>
    </el-card>
    <el-dialog v-model="editor.open" :title="editor.title" width="min(720px, calc(100vw - 32px))" append-to-body>
      <el-form :model="editor.form" label-width="90px">
        <el-form-item label="路由 ID"><el-input v-model="editor.form.routeId" /></el-form-item>
        <el-form-item label="路由名称"><el-input v-model="editor.form.routeName" /></el-form-item>
        <el-form-item label="目标地址"><el-input v-model="editor.form.uri" /></el-form-item>
        <el-form-item label="断言"><el-input v-model="editor.form.predicates" type="textarea" /></el-form-item>
        <el-form-item label="过滤器"><el-input v-model="editor.form.filters" type="textarea" /></el-form-item>
        <el-form-item label="顺序"><el-input-number v-model="editor.form.order" /></el-form-item>
        <el-form-item label="元数据"><el-input v-model="editor.form.metadata" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="editor.open = false">取消</el-button><el-button type="primary" :loading="editor.saving" @click="save">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageApi } from './api'

const props = { title: '网关路由管理', columns: [{ prop: 'routeId', label: '路由 ID' }, { prop: 'routeName', label: '路由名称' }, { prop: 'uri', label: '目标地址' }, { prop: 'order', label: '顺序' }] }
const loading = ref(false)
const rows = ref([])
const editor = reactive({ open: false, title: '', saving: false, form: {} })
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
function openCreate() {
  editor.title = '新增路由'
  editor.form = { routeId: '', routeName: '', uri: '', predicates: '', filters: '', order: 0, metadata: '' }
  editor.open = true
}
async function openEdit(row) {
  editor.title = `编辑路由：${row.routeId}`
  const result = await pageApi.routeDetail(row.id)
  editor.form = { ...(result.data || row) }
  editor.open = true
}
async function save() {
  editor.saving = true
  try {
    await pageApi.saveRoute(editor.form)
    ElMessage.success('路由保存成功')
    editor.open = false
    await load()
  } finally {
    editor.saving = false
  }
}
async function refresh() {
  await pageApi.refreshRoutes()
  ElMessage.success('网关路由已刷新')
  await load()
}
onMounted(load)
</script>
<style scoped>
.toolbar { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.table-scroll { width: 100%; overflow-x: auto; }
.compact-table :deep(.el-table__cell), .compact-table :deep(.cell) { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
