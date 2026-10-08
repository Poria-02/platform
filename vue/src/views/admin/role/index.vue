<template>
  <ResourcePage title="角色管理" section="组织与权限" description="分配功能菜单与数据权限。" singular="角色" :api="api" id-key="roleId" :columns="columns" :filters="filters" :fields="fields">
    <template #field-dsScope="{ form }">
      <template v-if="form.dsType === 1">
        <el-tree-select :model-value="scopeIds(form.dsScope)" :data="departments" :props="{ label: 'name', children: 'children' }" node-key="id" multiple show-checkbox check-strictly filterable style="width:100%" placeholder="选择允许访问的部门" @visible-change="loadDepartments" @update:model-value="form.dsScope = $event.join(',')" />
        <span v-if="departmentError" class="scope-error">{{ departmentError }}</span>
      </template>
      <span v-else>仅在数据权限为“自定义”时选择部门</span>
    </template>
    <template #row-actions="{ row }"><el-button link type="primary" @click="openMenus(row)">菜单权限</el-button></template>
  </ResourcePage>
  <el-dialog v-model="dialog.open" :title="`菜单权限 · ${dialog.roleName}`" width="min(680px, calc(100vw - 28px))" append-to-body>
    <el-tree ref="treeRef" :data="dialog.tree" node-key="id" show-checkbox :props="{ label: 'name', children: 'children' }" :default-checked-keys="dialog.checked" />
    <template #footer><el-button @click="dialog.open = false">取消</el-button><el-button type="primary" :loading="dialog.saving" @click="saveMenus">保存权限</el-button></template>
  </el-dialog>
</template>
<script setup>
import { nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const dataScopeOptions = [{ label: '全部', value: 0 }, { label: '自定义', value: 1 }, { label: '本级', value: 3 }]
const columns = [{ prop: 'roleName', label: '角色名称' }, { prop: 'roleCode', label: '角色编码' }, { prop: 'roleDesc', label: '描述', width: 220 }, { prop: 'dsType', label: '数据权限', options: dataScopeOptions }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'roleName', label: '角色名称' }, { prop: 'roleCode', label: '角色编码' }]
const fields = [{ prop: 'roleName', label: '角色名称', required: true }, { prop: 'roleCode', label: '角色编码', required: true }, { prop: 'roleDesc', label: '描述', type: 'textarea' }, { prop: 'dsType', label: '数据权限类型', type: 'select', options: dataScopeOptions, default: 0, required: true }, { prop: 'dsScope', label: '自定义部门' }]
const departments = ref([])
const departmentError = ref('')
let departmentsLoading = false
function scopeIds(value) { return String(value || '').split(',').map(id => id.trim()).filter(Boolean) }
async function loadDepartments(visible) {
  if (!visible || departmentsLoading) return
  departmentsLoading = true
  try {
    function normalize(nodes) { return nodes.map(node => ({ ...node, id: String(node.id), children: normalize(node.children || []) })) }
    departments.value = normalize(await api.departmentTree() || [])
    departmentError.value = ''
  } catch {
    departmentError.value = '部门加载失败，请重新打开选择框重试'
  } finally { departmentsLoading = false }
}
const treeRef = ref()
const dialog = reactive({ open: false, roleId: null, roleName: '', tree: [], checked: [], saving: false })
async function openMenus(row) {
  const [tree, checked] = await Promise.all([api.menuTree(), api.assignedMenuIds(row.roleId)])
  dialog.roleId = row.roleId
  dialog.roleName = row.roleName
  dialog.tree = tree || []
  dialog.checked = checked || []
  dialog.open = true
  await nextTick()
  treeRef.value?.setCheckedKeys(dialog.checked)
}
async function saveMenus() {
  dialog.saving = true
  try {
    const ids = [...new Set([...(treeRef.value?.getCheckedKeys(false) || []), ...(treeRef.value?.getHalfCheckedKeys() || [])])]
    await api.assignMenus({ roleId: dialog.roleId, menuIds: ids })
    ElMessage.success('菜单权限已更新')
    dialog.open = false
  } catch {
    // HTTP 层显示保存错误。
  } finally { dialog.saving = false }
}
</script>
<style scoped>
.scope-error { color: #a94637; font-size: 13px; }
</style>
