<template>
  <ResourcePage title="动态路由" section="系统管理" description="维护网关路由并按需刷新生效。" singular="路由" :api="api" :columns="columns" :fields="fields" :pagination="false">
    <template #actions="{ reload }"><el-button :loading="refreshing" @click="refresh(reload)">刷新网关路由</el-button></template>
  </ResourcePage>
</template>
<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const refreshing = ref(false)
const columns = [{ prop: 'routeId', label: '路由 ID' }, { prop: 'routeName', label: '名称' }, { prop: 'uri', label: '目标 URI', width: 220 }, { prop: 'predicates', label: '匹配规则', width: 220 }, { prop: 'order', label: '顺序' }]
const fields = [{ prop: 'routeId', label: '路由 ID', required: true }, { prop: 'routeName', label: '名称' }, { prop: 'uri', label: '目标 URI', required: true }, { prop: 'predicates', label: '匹配规则', type: 'textarea' }, { prop: 'filters', label: '过滤器', type: 'textarea' }, { prop: 'order', label: '顺序', type: 'number', default: 0 }]
async function refresh(reload) {
  refreshing.value = true
  try { await api.refresh(); ElMessage.success('网关路由已刷新'); await reload() }
  catch { /* HTTP 层显示错误 */ }
  finally { refreshing.value = false }
}
</script>
