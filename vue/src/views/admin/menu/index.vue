<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 组织与权限</div><h1>菜单管理</h1><p>定义导航结构。path 同时决定页面路由与前端目录。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input v-model="query.name" placeholder="搜索菜单名称" clearable @keyup.enter="search" />
        <el-input v-model="query.platform" placeholder="搜索平台" clearable @keyup.enter="search" />
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button text @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">

        <el-button @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增菜单</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="visibleRows" row-key="id" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="name" label="名称" :min-width="190" show-overflow-tooltip>
          <template #default="scope"><div class="tree-label" :style="{ paddingLeft: `${scope.row.__depth * 18}px` }"><button v-if="scope.row.__hasChildren" class="tree-label__toggle" type="button" :aria-expanded="expandedIds.has(scope.row.id)" @click="toggleNode(scope.row)"><span :class="{ 'tree-label__arrow--open': expandedIds.has(scope.row.id) }">›</span>{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</button><span v-else class="tree-label__leaf"><span class="tree-label__spacer" />{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</span></div></template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" :min-width="125" show-overflow-tooltip>
          <template #default="scope"><span class="menu-icon-cell"><MenuIcon :name="scope.row.icon" /><span>{{ scope.row.icon || '默认' }}</span></span></template>
        </el-table-column>
        <el-table-column prop="path" label="路由 path" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.path, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="permission" label="权限标识" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.permission, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.type, {}, typeChoices, scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="platform" label="平台" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.platform, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.sort, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">

            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </section>
    <EditDialog ref="editorRef" :can-create="canCreate" :can-edit="canEdit" :type-choices="typeChoices" @saved="afterMenuSaved" />
  </div>
</template>

<script setup>
import EditDialog from './EditDialog.vue'

import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import MenuIcon from '@/ui/MenuIcon.vue'
import { useSession } from '@/core/session'
import { clearMenuRoutes, installMenuRoutes } from '@/core/router'
import * as api from './api'

const session = useSession()

const menuTypes = [{ label: '菜单', value: '0' }, { label: '按钮', value: '1' }]

async function refreshMenus() {
  session.menusReady = false
  try {
    const menus = await session.ensureMenus()
    clearMenuRoutes()
    installMenuRoutes(menus)
  } catch { /* 当前页面已显示保存结果，导航将在下次加载时重试。 */ }
}

const canCreate = computed(() => Boolean(api.create))

const canEdit = computed(() => Boolean(api.update))

const canRemove = computed(() => Boolean(api.remove))

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const query = reactive({ name: '', platform: '' })

const typeChoices = computed(() => menuTypes)

const expandedIds = ref(new Set())
const visibleRows = computed(() => {
  const result = []
  function append(nodes, depth) {
    for (const node of nodes) {
      const { children, ...row } = node
      const hasChildren = Boolean(children?.length)
      result.push({ ...row, __depth: depth, __hasChildren: hasChildren })
      if (hasChildren && expandedIds.value.has(node.id)) append(children, depth + 1)
    }
  }
  append(rows.value, 0)
  return result
})
function toggleNode(row) {
  const next = new Set(expandedIds.value)
  if (next.has(row.id)) next.delete(row.id)
  else next.add(row.id)
  expandedIds.value = next
}
function expandAll() {
  const ids = new Set()
  function append(nodes) {
    for (const node of nodes) if (node.children?.length) { ids.add(node.id); append(node.children) }
  }
  append(rows.value)
  expandedIds.value = ids
}

async function load() {
  const sequence = ++loadSequence
  loading.value = true
  try {
    const params = { ...Object.fromEntries(Object.entries(query).filter(([, value]) => value !== '' && value !== null && value !== undefined)),  }
    const data = await api.list(params)
    if (sequence !== loadSequence) return
    rows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []

    if (Object.values(query).some(value => value !== '')) expandAll()
    loadError.value = ''
  } catch (error) {
    if (sequence === loadSequence) loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { return load() }
function resetSearch() { query.name = ''; query.platform = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条菜单吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')

    await load()
    await refreshMenus()
  } catch { /* 请求层显示错误。 */ }
}

onMounted(load)

const editorRef = ref()
function openCreate(defaults = {}) {
  if (canCreate.value) editorRef.value?.openCreate(defaults)
}
function openEdit(row) {
  if (canEdit.value) return editorRef.value?.openEdit(row)
}

async function afterMenuSaved() { await load(); await refreshMenus() }
</script>

<style scoped>.path-hint { grid-column: 1 / -1; padding: 14px; border-radius: 4px; background: #f6f6f6; color: #595959; font-size: 12px; line-height: 1.6; }.path-hint p { margin: 6px 0; }.path-hint code { color: #565656; font-weight: 700; }.menu-icon-cell { display: inline-flex; align-items: center; gap: 7px; color: #737373; }.menu-icon-cell .el-icon { color: #8d8d8d; font-size: 16px; }</style>
