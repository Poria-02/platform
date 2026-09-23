<template><ResourcePage title="公共参数" section="平台配置" description="维护服务使用的公共配置参数。" singular="参数" :api="api" id-key="publicId" :columns="columns" :filters="filters" :fields="fields" /></template>
<script setup>
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const columns = [{ prop: 'publicKey', label: '参数键', width: 180 }, { prop: 'publicValue', label: '参数值', width: 220 }, { prop: 'publicName', label: '名称' }, { prop: 'publicType', label: '参数类型', dictionary: 'param_type' }, { prop: 'status', label: '状态' }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'publicKey', label: '参数键' }, { prop: 'publicName', label: '名称' }]
const fields = [{ prop: 'publicKey', label: '参数键', required: true }, { prop: 'publicValue', label: '参数值', required: true }, { prop: 'publicName', label: '名称' }, { prop: 'publicType', label: '参数类型', type: 'select', dictionary: 'param_type', default: '0' }, { prop: 'status', label: '状态' }, { prop: 'remarks', label: '备注', type: 'textarea' }]
</script>
