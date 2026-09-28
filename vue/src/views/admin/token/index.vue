<template>
  <ResourcePage
    title="令牌管理"
    section="系统管理"
    description="查看在线令牌并撤销指定会话。"
    singular="令牌"
    :api="pageApi"
    id-key="accessToken"
    :columns="columns"
  >
    <template #row-actions="{ row, reload }">
      <el-button link type="primary" @click="showDetail(row)">查看</el-button>
      <el-button link type="danger" :loading="deletingToken === row.accessToken" :disabled="Boolean(deletingToken)" @click="removeToken(row, reload)">删除令牌</el-button>
    </template>
  </ResourcePage>

  <el-dialog v-model="detail.open" title="令牌详情" width="min(720px, calc(100vw - 28px))">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="用户">{{ detail.row.username || '—' }}</el-descriptions-item>
      <el-descriptions-item label="客户端">{{ detail.row.clientId || '—' }}</el-descriptions-item>
      <el-descriptions-item label="签发时间">{{ detail.row.issuedAt || '—' }}</el-descriptions-item>
      <el-descriptions-item label="过期时间">{{ detail.row.expiresAt || '—' }}</el-descriptions-item>
      <el-descriptions-item label="令牌">
        <span class="token-value">{{ detail.row.accessToken || '—' }}</span>
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ResourcePage from '@/components/ResourcePage.vue'
import { useSession } from '@/core/session'
import { readSession } from '@/core/token'
import { clearMenuRoutes } from '@/core/router'
import router from '@/core/router'
import * as api from './api'

const pageApi = { list: api.list }
const detail = reactive({ open: false, row: {} })
const deletingToken = ref('')
const session = useSession()

function showDetail(row) {
  detail.row = row
  detail.open = true
}

async function removeToken(row, reload) {
  try {
    await ElMessageBox.confirm('确认删除该令牌并结束对应会话吗？', '删除令牌', { type: 'warning' })
  } catch { return }

  deletingToken.value = row.accessToken
  try {
    const removed = await api.remove(row.accessToken)
    if (removed !== true) {
      ElMessage.warning('令牌不存在或已失效')
      await reload()
      return
    }
    ElMessage.success('令牌已删除')
    if (detail.row.accessToken === row.accessToken) detail.open = false
    if (readSession()?.accessToken === row.accessToken) {
      session.clearLocal()
      clearMenuRoutes()
      await router.replace('/login')
    } else {
      await reload()
    }
  } catch {
    // HTTP 层显示删除错误。
  } finally {
    deletingToken.value = ''
  }
}

const columns = [
  { prop: 'username', label: '用户' },
  { prop: 'clientId', label: '客户端' },
  { prop: 'issuedAt', label: '签发时间', width: 180 },
  { prop: 'expiresAt', label: '过期时间', width: 180 },
  { prop: 'accessToken', label: '令牌', width: 180, format: value => value ? `${String(value).slice(0, 8)}…${String(value).slice(-6)}` : '—' }
]
</script>

<style scoped>
.token-value { overflow-wrap: anywhere; user-select: text; }
</style>
