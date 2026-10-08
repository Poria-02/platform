<template><ResourcePage title="公共参数" section="平台配置" description="维护服务使用的公共配置参数。" singular="参数" :api="api" id-key="publicId" :columns="columns" :filters="filters" :fields="fields" :prepare-edit="prepareEdit" /></template>
<script setup>
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
// 公共参数的有效标识与用户冻结状态属于不同业务，按后端定义分别维护。
const statusOptions = [{ label: '正常', value: '1' }, { label: '冻结', value: '2' }]
const columns = [{ prop: 'publicKey', label: '参数键', width: 180 }, { prop: 'publicValue', label: '参数值', width: 220 }, { prop: 'publicName', label: '名称' }, { prop: 'publicType', label: '参数类型', dictionary: 'param_type' }, { prop: 'status', label: '状态', options: [...statusOptions, { label: '冻结', value: '0' }] }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'publicKey', label: '参数键' }, { prop: 'publicName', label: '名称' }]
const fields = [{ prop: 'publicKey', label: '参数键', required: true }, { prop: 'publicValue', label: '参数值', required: true }, { prop: 'publicName', label: '名称' }, { prop: 'publicType', label: '参数类型', type: 'select', dictionary: 'param_type', default: '0' }, { prop: 'status', label: '状态', type: 'select', options: statusOptions, default: '1', required: true }, { prop: 'remarks', label: '备注', type: 'textarea' }]
// 历史值 0 同样表示无效；编辑时回显到后端定义的冻结选项 2。
const prepareEdit = row => ({ ...row, status: String(row.status) === '0' ? '2' : row.status })
</script>
