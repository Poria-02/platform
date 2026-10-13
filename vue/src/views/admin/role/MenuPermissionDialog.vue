<template>
  <el-dialog class="business-dialog" v-model="dialog.open" :title="`菜单权限 · ${dialog.roleName}`" width="min(680px, calc(100vw - 28px))" append-to-body>
    <p class="menu-permission-hint">分别勾选页面和操作权限，保存时会自动包含上级菜单；所有角色默认保留个人中心查看和修改权限。</p>
    <el-tree ref="treeRef" :data="dialog.tree" node-key="id" show-checkbox check-strictly :props="{ label: 'name', children: 'children' }" :default-checked-keys="dialog.checked" />
    <template #footer><el-button @click="dialog.open = false">取消</el-button><el-button type="primary" :loading="dialog.saving" @click="saveMenus">保存权限</el-button></template>
  </el-dialog>
</template>

<script setup>

import { nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import * as api from './api'

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

defineExpose({ open: openMenus })

</script>

<style scoped>
.scope-error { color: #a94637; font-size: 13px; }
.scope-hint { margin: 8px 0 0; color: #737373; font-size: 13px; line-height: 1.6; }
.scope-hint.scope-error { color: #a94637; }
.menu-permission-hint { margin: 0 0 16px; color: #737373; font-size: 13px; }
</style>
