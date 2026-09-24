<template><ResourcePage title="用户管理" section="组织与权限" description="维护平台账号、所属部门和登录状态。" singular="用户" :api="api" id-key="userId" :columns="columns" :filters="filters" :fields="fields" :prepare-edit="prepareEdit" /></template>
<script setup>
import { computed, onMounted, ref } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const roles = ref([])
const lockOptions = [{ label: '正常', value: '0' }, { label: '锁定', value: '9' }]
const columns = [{ prop: 'username', label: '用户名' }, { prop: 'phone', label: '手机号' }, { prop: 'deptName', label: '部门' }, { prop: 'lockFlag', label: '锁定状态', format: value => lockOptions.find(option => option.value === String(value))?.label || value || '—' }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'username', label: '用户名' }, { prop: 'phone', label: '手机号' }]
const fields = computed(() => [
  { prop: 'username', label: '用户名', required: true },
  { prop: 'password', label: '密码', type: 'password', requiredOnCreate: true },
  { prop: 'phone', label: '手机号', required: true },
  { prop: 'deptId', label: '部门 ID', type: 'number' },
  { prop: 'role', label: '角色', type: 'select', multiple: true, required: true, options: roles.value.map(role => ({ label: role.roleName, value: role.roleId })) },
  { prop: 'lockFlag', label: '锁定状态', type: 'select', default: '0', options: lockOptions }
])
const prepareEdit = row => ({ ...row, password: '', role: row.roleList?.map(role => role.roleId) || [] })
onMounted(async () => { try { roles.value = await api.roles() || [] } catch { /* HTTP 层提示错误 */ } })
</script>
