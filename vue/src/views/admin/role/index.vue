<template>
  <ResourcePage title="角色管理" section="组织与权限" description="分配功能菜单与数据权限。" singular="角色" :api="api" id-key="roleId" :columns="columns" :filters="filters" :fields="fields">
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
const columns = [{ prop: 'roleName', label: '角色名称' }, { prop: 'roleCode', label: '角色编码' }, { prop: 'roleDesc', label: '描述', width: 220 }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'roleName', label: '角色名称' }, { prop: 'roleCode', label: '角色编码' }]
const fields = [{ prop: 'roleName', label: '角色名称', required: true }, { prop: 'roleCode', label: '角色编码', required: true }, { prop: 'roleDesc', label: '描述', type: 'textarea' }, { prop: 'dsType', label: '数据权限类型', type: 'number', default: 0 }]
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
