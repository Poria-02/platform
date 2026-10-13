<template>
  <el-dialog class="business-dialog" append-to-body v-model="dialog.open" title="日志详情" width="min(720px, calc(100vw - 28px))"><el-descriptions :column="1" border><el-descriptions-item v-for="([key, value]) in Object.entries(dialog.data)" :key="key" :label="key">{{ key === 'userType' ? userTypes[value] || value : value }}</el-descriptions-item></el-descriptions></el-dialog>
</template>

<script setup>
import { reactive } from 'vue'

import * as api from './api'

defineProps({
  userTypes: { type: Object, required: true }
})

const dialog = reactive({ open: false, data: {} })

async function showDetail(row) {
  try { dialog.data = await api.detail(row.id) || row; dialog.open = true }
  catch { /* HTTP 层显示错误 */ }
}

defineExpose({ open: showDetail })

</script>
