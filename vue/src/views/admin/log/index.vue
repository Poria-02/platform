<template><ResourcePage title="操作日志" section="审计与记录" description="查询平台管理操作记录。" :api="api" :columns="columns" :filters="filters" /></template>
<script setup>
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const columns = [{ prop: 'title', label: '标题' }, { prop: 'type', label: '类型', dictionary: 'log_type' }, { prop: 'createBy', label: '操作人' }, { prop: 'requestUri', label: '请求地址', width: 220 }, { prop: 'createTime', label: '时间', width: 180 }]
const filters = [{ prop: 'title', label: '标题' }, { prop: 'createBy', label: '操作人' }, { prop: 'type', label: '类型', type: 'select', dictionary: 'log_type' }]
</script>
