<template>
  <ResourcePage title="终端管理" section="系统管理" description="管理接入平台的 OAuth 客户端。" singular="终端" :api="api" id-key="clientId" :columns="columns" :filters="filters" :fields="fields">
    <template #actions="{ reload }"><el-button :loading="clearing" @click="clear(reload)">清除客户端缓存</el-button></template>
  </ResourcePage>
</template>
<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const clearing = ref(false)
const columns = [{ prop: 'clientId', label: '客户端 ID' }, { prop: 'scope', label: '授权范围' }, { prop: 'authorizedGrantTypes', label: '授权方式', width: 270, dictionary: 'grant_types', separator: ',' }, { prop: 'accessTokenValidity', label: '令牌有效期' }]
const filters = [{ prop: 'clientId', label: '客户端 ID' }]
const fields = [{ prop: 'clientId', label: '客户端 ID', required: true, immutable: true }, { prop: 'clientSecret', label: '客户端密钥', type: 'password', required: true }, { prop: 'scope', label: '授权范围', required: true }, { prop: 'authorizedGrantTypes', label: '授权方式', type: 'select', multiple: true, joinWith: ',', dictionary: 'grant_types' }, { prop: 'accessTokenValidity', label: '令牌有效期', type: 'number' }, { prop: 'refreshTokenValidity', label: '刷新有效期', type: 'number' }]
async function clear(reload) {
  clearing.value = true
  try { await api.clearCache(); ElMessage.success('客户端缓存已清除'); await reload() }
  catch { /* HTTP 层显示错误 */ }
  finally { clearing.value = false }
}
</script>
