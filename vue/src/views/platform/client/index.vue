<template>
  <div class="app-container">
    <el-card>
      <template #header>OAuth 客户端管理</template>
      <div class="toolbar">
        <el-button type="primary" @click="load">刷新</el-button>
        <el-button type="success" @click="openCreate">新增客户端</el-button>
        <el-button type="warning" @click="clearCache">清理客户端缓存</el-button>
      </div>
      <div class="table-scroll">
        <el-table v-loading="loading" :data="rows" border class="compact-table">
          <el-table-column v-for="column in columns" :key="column.prop" :prop="column.prop" :label="column.label" :min-width="column.width" show-overflow-tooltip>
            <template #default="scope">{{ display(scope.row[column.prop]) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="openDetail(scope.row)">查看</el-button>
              <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
              <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <Pagination v-show="total > 0" v-model:page="page.current" v-model:limit="page.size" :total="total" @pagination="load" />
    </el-card>
    <el-dialog v-model="editor.open" :title="editor.title" width="min(720px, calc(100vw - 32px))" append-to-body>
      <el-form :model="editor.form" label-width="130px">
        <el-form-item label="客户端 ID"><el-input v-model="editor.form.clientId" :disabled="editor.editing" /></el-form-item>
        <el-form-item label="客户端密钥"><el-input v-model="editor.form.clientSecret" show-password /></el-form-item>
        <el-form-item label="作用域"><el-input v-model="editor.form.scope" /></el-form-item>
        <el-form-item label="授权方式"><el-input v-model="editor.form.authorizedGrantTypes" /></el-form-item>
        <el-form-item label="重定向地址"><el-input v-model="editor.form.webServerRedirectUri" /></el-form-item>
        <el-form-item label="令牌有效期(秒)"><el-input-number v-model="editor.form.accessTokenValidity" :min="0" /></el-form-item>
        <el-form-item label="刷新令牌有效期(秒)"><el-input-number v-model="editor.form.refreshTokenValidity" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="editor.form.additionalInformation" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editor.open = false">取消</el-button>
        <el-button type="primary" :loading="editor.saving" @click="save">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="detail.open" title="客户端详情" width="min(760px, calc(100vw - 32px))" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item v-for="item in detail.items" :key="item.label" :label="item.label">{{ display(item.value) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageApi } from './api'

const columns = [
  { prop: 'clientId', label: '客户端 ID', width: 150 },
  { prop: 'scope', label: '作用域', width: 140 },
  { prop: 'authorizedGrantTypes', label: '授权方式', width: 300 },
  { prop: 'accessTokenValidity', label: '令牌有效期', width: 130 }
]
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const editor = reactive({ open: false, title: '', editing: false, saving: false, form: {} })
const detail = reactive({ open: false, items: [] })
const display = value => value === null || value === undefined || value === '' ? '-' : String(value)

async function load() {
  loading.value = true
  try {
    const result = (await pageApi.clients({ current: page.current, size: page.size })).data || {}
    rows.value = result.records || []
    total.value = Number(result.total || 0)
  } catch (_) {
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
function openCreate() {
  editor.title = '新增客户端'
  editor.editing = false
  editor.form = { clientId: '', clientSecret: '', scope: 'server', authorizedGrantTypes: '', webServerRedirectUri: '', accessTokenValidity: 0, refreshTokenValidity: 0, additionalInformation: '' }
  editor.open = true
}
async function openEdit(row) {
  const result = await pageApi.client(row.clientId)
  editor.title = `编辑客户端：${row.clientId}`
  editor.editing = true
  editor.form = { ...(result.data || row) }
  editor.open = true
}
async function openDetail(row) {
  const result = await pageApi.clientDetails(row.clientId)
  detail.items = Object.entries(result.data || row).map(([label, value]) => ({ label, value }))
  detail.open = true
}
async function save() {
  editor.saving = true
  try {
    await (editor.editing ? pageApi.updateClient(editor.form) : pageApi.saveClient(editor.form))
    ElMessage.success('客户端保存成功')
    editor.open = false
    await load()
  } finally {
    editor.saving = false
  }
}
async function remove(row) {
  await ElMessageBox.confirm(`确认删除客户端 ${row.clientId} 吗？`, '系统提示', { type: 'warning' })
  await pageApi.deleteClient(row.clientId)
  ElMessage.success('删除成功')
  await load()
}
async function clearCache() {
  await ElMessageBox.confirm('确认清理全部客户端缓存吗？', '系统提示', { type: 'warning' })
  await pageApi.clearClientCache()
  ElMessage.success('客户端缓存已清理')
}
onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.table-scroll { width: 100%; overflow-x: auto; }
.compact-table :deep(.el-table__cell), .compact-table :deep(.cell) { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
