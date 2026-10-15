<template>
  <el-dialog class="business-dialog" v-model="dialog.open" :title="`菜单权限 · ${dialog.roleName}`" width="min(680px, calc(100vw - 28px))" append-to-body>
    <p class="menu-permission-hint">分别勾选页面和操作权限，取消菜单时会同时取消下级权限，保存时会自动包含上级菜单；所有角色默认保留个人中心查看和修改权限，以及站内信、公示公告查看入口。消息查看、已发布公告分页和已读无需操作权限；站内信发布、公告草稿查看及公告新增、修改、删除、发布权限仅配置给系统管理员，公告发布权限单独配置。</p>
    <el-tree ref="treeRef" :data="dialog.tree" node-key="id" show-checkbox check-strictly :props="{ label: 'name', children: 'children' }" :default-checked-keys="dialog.checked" @check="handleMenuCheck" />
    <template #footer><el-button @click="dialog.open = false">取消</el-button><el-button :disabled="!canMenus" type="primary" v-stable-loading="dialog.saving" :loading="dialog.saving" @click="saveMenus">保存权限</el-button></template>
  </el-dialog>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as api from './api'
import { useResourcePermissions } from '@/core/permissions'

const { canMenus } = useResourcePermissions('role')

const treeRef = ref()

const dialog = reactive({ open: false, roleId: null, roleName: '', tree: [], checked: [], saving: false })

function handleMenuCheck(menu, { checkedKeys }) {
  if (checkedKeys.some(id => String(id) === String(menu.id))) return
  function uncheckDescendants(nodes) {
    for (const node of nodes) {
      treeRef.value?.setChecked(node.id, false, false)
      uncheckDescendants(node.children || [])
    }
  }
  uncheckDescendants(menu.children || [])
}

async function openMenus(row) {
  if (!canMenus.value || dialog.saving) return
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
  if (!canMenus.value || dialog.saving) return
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
