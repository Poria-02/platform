<template>
  <ResourcePage title="菜单管理" section="组织与权限" description="定义导航结构。path 同时决定页面路由与前端目录。" singular="菜单" :api="api" id-key="id" :columns="columns" :filters="filters" :fields="fields" :pagination="false" tree-mode :prepare-edit="prepareEdit" @saved="refreshMenus" @removed="refreshMenus">
    <template #cell-icon="{ row }"><span class="menu-icon-cell"><MenuIcon :name="row.icon" /><span>{{ row.icon || '默认' }}</span></span></template>
    <template #field-icon="{ form }"><IconPicker v-model="form.icon" /></template>
    <template #form-hint="{ form }"><div class="path-hint"><strong>目录映射</strong><p>例如 <code>/admin/menu/index</code> 对应：</p><code>src/views/admin/menu/index.vue</code><br><code>src/views/admin/menu/api.js</code><p>相对路径按父菜单路径展开。按钮类型 1 不生成页面路由。</p><p v-if="form.path && form.path.startsWith('/')">当前路径将查找对应目录下的 <code>index.vue</code> 和 <code>api.js</code></p></div></template>
  </ResourcePage>
</template>
<script setup>
import { defineAsyncComponent } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import MenuIcon from '@/ui/MenuIcon.vue'
import { useSession } from '@/core/session'
import { clearMenuRoutes, installMenuRoutes } from '@/core/router'
import * as api from './api'
const IconPicker = defineAsyncComponent(() => import('@/ui/IconPicker.vue'))
const session = useSession()
const menuTypes = [{ label: '菜单', value: '0' }, { label: '按钮', value: '1' }]
const columns = [{ prop: 'name', label: '名称', width: 190 }, { prop: 'icon', label: '图标', width: 125 }, { prop: 'path', label: '路由 path', width: 220 }, { prop: 'permission', label: '权限标识', width: 180 }, { prop: 'type', label: '类型', format: value => menuTypes.find(option => option.value === String(value))?.label || value || '—' }, { prop: 'platform', label: '平台' }, { prop: 'sort', label: '排序' }]
const filters = [{ prop: 'name', label: '菜单名称' }, { prop: 'platform', label: '平台' }]
const fields = [{ prop: 'name', label: '菜单名称', required: true }, { prop: 'path', label: '路由 path' }, { prop: 'permission', label: '权限标识' }, { prop: 'parentId', label: '上级菜单 ID', type: 'number', min: -1, default: -1 }, { prop: 'icon', label: '图标' }, { prop: 'type', label: '菜单类型', type: 'select', required: true, default: '0', options: menuTypes }, { prop: 'platform', label: '所属平台' }, { prop: 'sort', label: '排序', type: 'number', default: 0 }]
const prepareEdit = row => ({ menuId: row.id, ...Object.fromEntries(fields.map(field => [field.prop, row[field.prop] ?? field.default ?? ''])) })
async function refreshMenus() {
  session.menusReady = false
  try {
    const menus = await session.ensureMenus()
    clearMenuRoutes()
    installMenuRoutes(menus)
  } catch { /* 当前页面已显示保存结果，导航将在下次加载时重试。 */ }
}
</script>
<style scoped>.path-hint { grid-column: 1 / -1; padding: 14px; border-radius: 9px; background: #eef8f3; color: #356455; font-size: 12px; line-height: 1.6; }.path-hint p { margin: 6px 0; }.path-hint code { color: #116b58; font-weight: 700; }.menu-icon-cell { display: inline-flex; align-items: center; gap: 7px; color: #60768a; }.menu-icon-cell .el-icon { color: #33a983; font-size: 16px; }</style>
