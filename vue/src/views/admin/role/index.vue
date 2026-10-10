<template>
  <ResourcePage title="角色管理" section="组织与权限" description="分配功能菜单，并设置角色可访问的部门数据范围。" singular="角色" :api="api" id-key="roleId" :columns="columns" :filters="filters" :fields="fields">
    <template #cell-dsType="{ value }"><span :class="{ 'scope-error': !scopeOption(value) }">{{ scopeOption(value)?.label || `未支持（${value}）` }}</span></template>
    <template #field-dsType="{ form }">
      <el-select v-model="form.dsType" style="width:100%" placeholder="请选择数据权限" @change="form.dsScope = ''"><el-option v-for="option in dataScopeOptions" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      <p class="scope-hint" :class="{ 'scope-error': !scopeOption(form.dsType) }">{{ scopeOption(form.dsType)?.description || '此类型尚未支持，请选择以上四种数据权限之一。' }}</p>
    </template>
    <template #field-dsScope="{ form }">
      <template v-if="form.dsType === 1">
        <el-tree-select :model-value="scopeIds(form.dsScope)" :data="departments" :props="{ label: 'name', children: 'children' }" node-key="id" multiple show-checkbox check-strictly filterable style="width:100%" placeholder="选择允许访问的部门" @visible-change="loadDepartments" @update:model-value="form.dsScope = $event.join(',')" />
        <span v-if="departmentError" class="scope-error">{{ departmentError }}</span>
        <p class="scope-hint">只允许选中的部门。选择父部门不会自动包含下级，需要逐个勾选。</p>
      </template>
      <span v-else class="scope-hint">{{ form.dsType === 0 ? '全部数据无需选择部门。' : '无需选择部门，范围由登录用户所属部门确定。' }}</span>
    </template>
    <template #form-hint><p class="scope-hint">用户拥有多个角色时，数据范围合并；任一角色为“全部”时，可访问全部部门数据。</p></template>
    <template #row-actions="{ row }"><el-button link type="primary" @click="openMenus(row)">菜单权限</el-button></template>
  </ResourcePage>
  <el-dialog v-model="dialog.open" :title="`菜单权限 · ${dialog.roleName}`" width="min(680px, calc(100vw - 28px))" append-to-body>
    <p class="menu-permission-hint">分别勾选页面和操作权限，保存时会自动包含上级菜单。</p>
    <el-tree ref="treeRef" :data="dialog.tree" node-key="id" show-checkbox check-strictly :props="{ label: 'name', children: 'children' }" :default-checked-keys="dialog.checked" />
    <template #footer><el-button @click="dialog.open = false">取消</el-button><el-button type="primary" :loading="dialog.saving" @click="saveMenus">保存权限</el-button></template>
  </el-dialog>
</template>
<script setup>
import { nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const dataScopeOptions = [
  { label: '全部', value: 0, description: '允许访问全部部门的数据。' },
  { label: '自定义', value: 1, description: '允许访问指定部门的数据，需要在下方选择部门。' },
  { label: '本部门', value: 2, description: '仅允许访问登录用户所属部门的数据，不包含下级部门。' },
  { label: '本部门及下级', value: 3, description: '允许访问登录用户所属部门及其所有下级部门的数据。' }
]
function scopeOption(value) { return dataScopeOptions.find(option => option.value === Number(value)) }
const columns = [{ prop: 'roleName', label: '角色名称' }, { prop: 'roleCode', label: '角色编码' }, { prop: 'roleDesc', label: '描述', width: 220 }, { prop: 'dsType', label: '数据权限', options: dataScopeOptions }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'roleName', label: '角色名称' }, { prop: 'roleCode', label: '角色编码' }]
const fields = [{ prop: 'roleName', label: '角色名称', required: true }, { prop: 'roleCode', label: '角色编码', required: true }, { prop: 'roleDesc', label: '描述', type: 'textarea' }, { prop: 'dsType', label: '数据权限', type: 'select', options: dataScopeOptions, default: 2, required: true }, { prop: 'dsScope', label: '数据权限范围（自定义部门）' }]
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
    const ids = new Set(treeRef.value?.getCheckedKeys(false) || [])
    function includeParents(nodes, parents = []) {
      for (const node of nodes) {
        if (ids.has(node.id)) for (const id of parents) ids.add(id)
        includeParents(node.children || [], [...parents, node.id])
      }
    }
    includeParents(dialog.tree)
    await api.assignMenus({ roleId: dialog.roleId, menuIds: [...ids] })
    ElMessage.success('菜单权限已更新')
    dialog.open = false
  } catch {
    // HTTP 层显示保存错误。
  } finally { dialog.saving = false }
}
</script>
<style scoped>
.scope-error { color: #a94637; font-size: 13px; }
.scope-hint { margin: 8px 0 0; color: #60768a; font-size: 13px; line-height: 1.6; }
.scope-hint.scope-error { color: #a94637; }
.menu-permission-hint { margin: 0 0 16px; color: #60768a; font-size: 13px; }
</style>
