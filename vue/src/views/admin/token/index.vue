<template>
  <ResourcePage
    title="令牌管理"
    section="系统管理"
    description="查看在线令牌并撤销指定会话。"
    singular="令牌"
    :api="api"
    id-key="accessToken"
    :columns="columns"
  />
</template>

<script setup>
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'

const columns = [
  { prop: 'username', label: '用户' },
  { prop: 'clientId', label: '客户端' },
  { prop: 'issuedAt', label: '签发时间', width: 180 },
  { prop: 'expiresAt', label: '过期时间', width: 180 },
  { prop: 'accessToken', label: '令牌', width: 180, format: value => value ? `${String(value).slice(0, 8)}…${String(value).slice(-6)}` : '—' }
]
</script>
