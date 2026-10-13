<template>
  <el-dialog class="business-dialog" append-to-body :model-value="detail.open" @close="emit('close')" title="令牌详情" width="min(720px, calc(100vw - 28px))">
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

defineProps({
  detail: { type: Object, required: true }
})
const emit = defineEmits(["close"])

</script>

<style scoped>
.token-value { overflow-wrap: anywhere; user-select: text; }
</style>
