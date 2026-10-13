<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 组织与权限</div><h1>部门管理</h1><p>维护组织层级、部门信息及上下级关系。</p></div>
    </header>
    <div class="resource-toolbar">
      <div v-if="canQuery" class="filter-bar">
        <el-input v-model="query.name" placeholder="搜索部门名称" clearable @keyup.enter="search" />
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">
        <el-button @click="expandAll()">展开全部</el-button><el-button @click="collapseAll()">收起全部</el-button>
        <el-button v-if="canQuery" @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增部门</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>
      <div v-if="dictionaryError" class="resource-error" role="alert"><span>{{ dictionaryError }}</span><el-button link type="primary" @click="loadTypeDictionary">重试</el-button></div>
      <el-table v-loading="loading" :data="visibleRows" row-key="id" class="data-table" :empty-text="canQuery ? '暂无数据' : '未配置查询部门权限'" style="width:100%">
        <el-table-column prop="name" label="部门名称" :min-width="250" show-overflow-tooltip>
          <template #default="scope"><div class="tree-label" :style="{ paddingLeft: `${scope.row.__depth * 18}px` }"><button v-if="scope.row.__hasChildren" class="tree-label__toggle" type="button" :aria-expanded="expandedIds.has(scope.row.id)" @click="toggleNode(scope.row)"><span :class="{ 'tree-label__arrow--open': expandedIds.has(scope.row.id) }">›</span>{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</button><span v-else class="tree-label__leaf"><span class="tree-label__spacer" />{{ displayResourceValue(scope.row.name, {}, [], scope.row) }}</span></div></template>
        </el-table-column>
        <el-table-column prop="id" label="部门编号" :min-width="120" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.id, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button v-if="session.hasPermission(permissions.create)" link type="primary" @click="openCreate({ parentId: scope.row.id })">新增下级</el-button>
            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </section>
    <EditDialog ref="editorRef" :parent-options="parentOptions" :can-create="canCreate" :can-edit="canEdit" :type-choices="typeChoices" @saved="load" />
  </div>
</template>

<script setup>
import EditDialog from './EditDialog.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue, optionsForField } from '@/core/resourceValues'
import { useSession } from '@/core/session'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'

const nextListRequest = useRequestController()

const session = useSession()

const departments = ref([])

const permissions = { query: 'sys_dept_tree', create: 'sys_dept_add', update: 'sys_dept_edit', remove: 'sys_dept_del' }

const canQuery = computed(() => session.hasPermission(permissions.query))

function parentOptions(deptId) {
  function exclude(nodes) {
    return nodes.filter(node => String(node.id) !== String(deptId)).map(node => ({ ...node, children: exclude(node.children || []) }))
  }
  return [{ id: 0, name: '顶级部门', children: exclude(departments.value) }]
}

function removeMessage(row) {
  return `确认删除“${row.name}”及其所有下级部门吗？请先处理关联的用户。`
}

async function listDepartments({ name = '' } = {}, request) {
  const tree = await api.tree(request) || []
  if (request.signal.aborted) return []
  departments.value = tree
  const keyword = name.trim()
  function filter(nodes) {
    return nodes.flatMap(node => {
      const children = filter(node.children || [])
      return node.name?.includes(keyword) ? [node] : children.length ? [{ ...node, children }] : []
    })
  }
  return keyword ? filter(departments.value) : departments.value
}

const canCreate = computed(() => Boolean(api.create) && session.hasPermission(permissions.create))

const canEdit = computed(() => Boolean(api.update) && session.hasPermission(permissions.update))

const canRemove = computed(() => Boolean(api.remove) && session.hasPermission(permissions.remove))

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const query = reactive({ name: '' })

const { syncListRoute } = useListRouteState({ query, reload: load, isLoading: () => loading.value })

const dictionaryItems = ref([])
const dictionaryError = ref('')
const typeChoices = computed(() => optionsForField({ dictionary: 'dept_type' }, dictionaryItems.value))

let typeDictionaryRequest
async function loadTypeDictionary() {
  if (typeDictionaryRequest) return typeDictionaryRequest
  typeDictionaryRequest = (async () => {
    try {
      const items = await api.typeItems()
      if (!Array.isArray(items)) throw new Error('部门类型返回格式错误')
      dictionaryItems.value = items.map(item => ({ ...item, type: 'dept_type' }))
      dictionaryError.value = items.length ? '' : '部门类型暂无可选项，请在字典管理中配置'
      return items.length > 0
    } catch {
      dictionaryItems.value = []
      dictionaryError.value = '部门类型加载失败，请重试'
      return false
    }
  })()
  try { return await typeDictionaryRequest }
  finally { typeDictionaryRequest = undefined }
}

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
function collapseAll() { expandedIds.value = new Set() }

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  if (!canQuery.value) {
    rows.value = []
    departments.value = []
    loadError.value = ''
    loading.value = false
    return
  }
  loading.value = true
  try {
    const params = await syncListRoute()
    if (!params || sequence !== loadSequence) return
    void loadTypeDictionary()
    const data = await listDepartments(params, request)
    if (sequence !== loadSequence) return
    rows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []

    if (Object.values(query).some(value => value !== '')) expandAll()
    loadError.value = ''
  } catch (error) {
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { return load() }
function resetSearch() { query.name = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm(removeMessage(row), '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.id)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')

    await load()

  } catch { /* 请求层显示错误。 */ }
}

onMounted(load)

const editorRef = ref()
async function openCreate(defaults = {}) {
  if (!canCreate.value) return
  if (!(await loadTypeDictionary())) { ElMessage.error(dictionaryError.value); return }
  editorRef.value?.openCreate(defaults)
}
async function openEdit(row) {
  if (!canEdit.value) return
  if (!(await loadTypeDictionary())) { ElMessage.error(dictionaryError.value); return }
  return editorRef.value?.openEdit(row)
}
</script>
