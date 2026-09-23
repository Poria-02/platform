<template>
  <ResourcePage title="版本管理" section="系统管理" description="管理移动端发布版本。" singular="版本" :api="api" :columns="columns" :filters="filters" :fields="fields" />
</template>
<script setup>
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const columns = [{ prop: 'appName', label: '应用', dictionary: 'app_name' }, { prop: 'appOs', label: '系统', dictionary: 'app_os' }, { prop: 'version', label: '版本' }, { prop: 'appUpdateType', label: '更新方式', dictionary: 'app_update_type' }, { prop: 'createTime', label: '发布时间', width: 180 }]
const filters = [{ prop: 'appName', label: '应用', type: 'select', dictionary: 'app_name' }, { prop: 'appOs', label: '系统', type: 'select', dictionary: 'app_os' }]
const fields = [{ prop: 'appName', label: '应用', type: 'select', dictionary: 'app_name', required: true }, { prop: 'appOs', label: '系统', type: 'select', dictionary: 'app_os', required: true }, { prop: 'version', label: '版本', required: true }, { prop: 'appUpdateType', label: '更新方式', type: 'select', dictionary: 'app_update_type' }, { prop: 'url', label: '下载地址' }, { prop: 'prompt', label: '更新提示', type: 'textarea' }]
</script>
