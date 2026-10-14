<template>
  <el-dialog class="business-dialog" append-to-body :model-value="detail.open" :title="kind === 'sms' ? '短信发送详情' : '微信发送详情'" width="min(760px, calc(100vw - 32px))" @close="emit('close')">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="记录 ID">{{ detail.row.id }}</el-descriptions-item>
      <el-descriptions-item :label="kind === 'sms' ? '接收手机号' : '接收账户'">{{ displayResourceValue(kind === 'sms' ? detail.row.phone : detail.row.accountId) }}</el-descriptions-item>
      <el-descriptions-item label="模板 Key">{{ displayResourceValue(detail.row.templateKey) }}</el-descriptions-item>
      <el-descriptions-item label="发送状态">{{ displayResourceValue(detail.row.status, {}, sendStatuses) }}</el-descriptions-item>
      <el-descriptions-item label="发送时间">{{ displayResourceValue(detail.row.createTime) }}</el-descriptions-item>
      <el-descriptions-item v-if="kind === 'sms'" label="短信平台">{{ displayResourceValue(detail.row.platform, {}, smsPlatforms) }}</el-descriptions-item>
      <el-descriptions-item v-if="kind === 'sms'" label="业务类型">{{ displayResourceValue(detail.row.businessType) }}</el-descriptions-item>
      <el-descriptions-item v-if="kind === 'wx'" label="微信消息 ID">{{ displayResourceValue(detail.row.msgId) }}</el-descriptions-item>
      <el-descriptions-item v-if="kind === 'wx'" label="机构 ID">{{ displayResourceValue(detail.row.orgId) }}</el-descriptions-item>
      <el-descriptions-item label="任务 ID">{{ displayResourceValue(detail.row.taskId) }}</el-descriptions-item>
      <el-descriptions-item label="发送内容"><pre class="log-text">{{ formatLogText(detail.row.content) }}</pre></el-descriptions-item>
      <el-descriptions-item label="发送参数"><pre class="log-text">{{ formatLogText(detail.row.params) }}</pre></el-descriptions-item>
      <el-descriptions-item label="发送结果"><pre class="log-text">{{ formatLogText(detail.row.result) }}</pre></el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { displayResourceValue } from '@/core/resourceValues'
import { smsPlatforms } from '../template/options'
import { formatLogText, sendStatuses } from './options'

defineProps({ detail: { type: Object, required: true }, kind: { type: String, required: true } })
const emit = defineEmits(['close'])
</script>

<style scoped>
.log-text { margin: 0; white-space: pre-wrap; overflow-wrap: anywhere; font: inherit; line-height: 1.7; }
</style>
