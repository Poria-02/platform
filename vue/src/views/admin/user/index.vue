<template>
  <ResourcePage title="用户管理" section="组织与权限" description="维护平台账号、所属部门和登录状态。" singular="用户" :api="api" id-key="userId" :columns="columns" :filters="filters" :fields="fields" :prepare-edit="prepareEdit">
    <template #field-deptId="{ form }">
      <el-tree-select v-model="form.deptId" :data="departments" :props="{ label: 'name', children: 'children' }" node-key="id" check-strictly filterable style="width:100%" placeholder="请选择所属部门" @visible-change="loadDepartments" />
      <span v-if="departmentError" class="department-error">{{ departmentError }}</span>
    </template>
  </ResourcePage>
</template>
<script setup>
import { computed, onMounted, ref } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import { tree as departmentTree } from '@/views/admin/dept/api'
import * as api from './api'
const roles = ref([])
const departments = ref([])
const departmentError = ref('')
let departmentsLoading = false
const lockOptions = [{ label: '正常', value: '0' }, { label: '冻结', value: '9' }]
const columns = [{ prop: 'username', label: '用户名' }, { prop: 'phone', label: '手机号' }, { prop: 'deptName', label: '部门' }, { prop: 'lockFlag', label: '状态', dictionary: 'status_type', options: lockOptions }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'username', label: '用户名' }, { prop: 'phone', label: '手机号' }]
const fields = computed(() => [
  { prop: 'username', label: '用户名', required: true },
  { prop: 'password', label: '密码', type: 'password', requiredOnCreate: true },
  { prop: 'phone', label: '手机号', required: true },
  { prop: 'deptId', label: '所属部门', required: true },
  { prop: 'role', label: '角色', type: 'select', multiple: true, required: true, options: roles.value.map(role => ({ label: role.roleName, value: role.roleId })) },
  { prop: 'lockFlag', label: '状态', type: 'select', dictionary: 'status_type', default: '0', options: lockOptions }
])
const prepareEdit = row => ({ ...row, password: '', role: row.roleList?.map(role => role.roleId) || [] })
async function loadDepartments(visible = true) {
  if (!visible || departmentsLoading) return
  departmentsLoading = true
  try {
    departments.value = await departmentTree() || []
    departmentError.value = ''
  } catch { departmentError.value = '部门加载失败，请重新打开选择框重试' }
  finally { departmentsLoading = false }
}
onMounted(async () => { try { roles.value = await api.roles() || [] } catch { /* HTTP 层提示错误 */ } })
onMounted(loadDepartments)
</script>
<style scoped>.department-error { color: #a94637; font-size: 13px; }</style>
