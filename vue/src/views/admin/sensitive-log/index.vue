<template><ResourcePage title="敏感数据日志" section="审计与记录" description="审阅敏感数据访问与操作记录。" :api="api" :columns="columns" :filters="filters"><template #row-actions="{ row }"><el-button link type="primary" @click="showDetail(row)">详情</el-button></template></ResourcePage><el-dialog v-model="dialog.open" title="日志详情" width="min(720px, calc(100vw - 28px))"><el-descriptions :column="1" border><el-descriptions-item v-for="([key, value]) in Object.entries(dialog.data)" :key="key" :label="key">{{ value }}</el-descriptions-item></el-descriptions></el-dialog></template>
<script setup>
import { reactive } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const columns = [{ prop: 'sensitiveInfo', label: '敏感信息', width: 230 }, { prop: 'createName', label: '操作人' }, { prop: 'userType', label: '用户类型' }, { prop: 'createTime', label: '时间', width: 180 }]
const filters = [{ prop: 'createName', label: '操作人' }, { prop: 'createBy', label: '操作人 ID' }]
const dialog = reactive({ open: false, data: {} })
async function showDetail(row) {
  try { dialog.data = await api.detail(row.id) || row; dialog.open = true }
  catch { /* HTTP 层显示错误 */ }
}
</script>
