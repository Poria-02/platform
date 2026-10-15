<template>
  <el-dialog class="business-dialog" append-to-body :model-value="detail.open" title="模板详情" width="min(760px, calc(100vw - 32px))" @close="emit('close')">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="模板 ID">{{ detail.row.id }}</el-descriptions-item>
      <el-descriptions-item label="标题">{{ detail.row.title }}</el-descriptions-item>
      <el-descriptions-item label="模板 Key">{{ detail.row.templateKey }}</el-descriptions-item>
      <el-descriptions-item label="类型">{{ displayResourceValue(detail.row.type, {}, templateTypes) }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ displayResourceValue(detail.row.status, {}, templateStatuses) }}</el-descriptions-item>
      <el-descriptions-item label="分类">{{ displayResourceValue(detail.row.category) }}</el-descriptions-item>
      <el-descriptions-item label="模板内容"><pre class="template-text">{{ detail.row.content || '—' }}</pre></el-descriptions-item>
      <el-descriptions-item label="跳转 URL"><span class="template-text">{{ detail.row.url || '—' }}</span></el-descriptions-item>
      <el-descriptions-item label="参数映射配置"><pre class="template-text">{{ detail.row.paramMapping || '—' }}</pre></el-descriptions-item>
      <el-descriptions-item label="创建人">{{ displayResourceValue(detail.row.createBy) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ displayResourceValue(detail.row.createTime) }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { displayResourceValue } from '@/core/resourceValues'
import { templateStatuses, templateTypes } from './options'

defineProps({ detail: { type: Object, required: true } })
const emit = defineEmits(['close'])
</script>

<style scoped>
.template-text { margin: 0; white-space: pre-wrap; overflow-wrap: anywhere; font: inherit; line-height: 1.7; }
</style>
