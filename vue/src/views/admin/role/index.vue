<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 组织与权限</div><h1>角色管理</h1><p>分配功能菜单，并设置角色可访问的部门数据范围。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input v-model="query.roleName" placeholder="搜索角色名称" clearable @keyup.enter="search" />
        <el-input v-model="query.roleCode" placeholder="搜索角色编码" clearable @keyup.enter="search" />
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">

        <el-button @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增角色</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>

      <el-table v-loading="loading" :data="rows" row-key="roleId" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="roleName" label="角色名称" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.roleName, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="roleCode" label="角色编码" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.roleCode, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="roleDesc" label="描述" :min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.roleDesc, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="dsType" label="数据权限" :min-width="130" show-overflow-tooltip>
          <template #default="scope"><span :class="{ 'scope-error': !scopeOption(scope.row.dsType) }">{{ scopeOption(scope.row.dsType)?.label || `未支持（${scope.row.dsType}）` }}</span></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createTime, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <el-button v-if="canMenus" link type="primary" @click="openMenus(scope.row)">菜单权限</el-button>
            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination :current-page="page.current" :page-size="page.size" :disabled="loading" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @update:page-size="changePageSize" @update:current-page="changePage" />
      </div>
    </section>
    <EditDialog ref="editorRef" :data-scope-options="dataScopeOptions" :scope-option="scopeOption" :departments="departments" :department-error="departmentError" :scope-ids="scopeIds" :load-departments="loadDepartments" :can-create="canCreate" :can-edit="canEdit" :ds-type-choices="dsTypeChoices" @saved="load" />
  </div>
  <MenuPermissionDialog ref="permissionRef" />
</template>

<script setup>
import MenuPermissionDialog from './MenuPermissionDialog.vue'
import EditDialog from './EditDialog.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue } from '@/core/resourceValues'
import * as api from './api'
import { useRequestController } from '@/core/requestController'
import { useListRouteState } from '@/core/listRouteState'
import { useResourcePermissions } from '@/core/permissions'

const nextListRequest = useRequestController()

const { canCreate, canEdit, canRemove, canMenus } = useResourcePermissions('role')

const dataScopeOptions = [
  { label: '全部', value: 0, description: '允许访问全部部门的数据。' },
  { label: '自定义', value: 1, description: '允许访问指定部门的数据，需要在下方选择部门。' },
  { label: '本部门', value: 2, description: '仅允许访问登录用户所属部门的数据，不包含下级部门。' },
  { label: '本部门及下级', value: 3, description: '允许访问登录用户所属部门及其所有下级部门的数据。' }
]

function scopeOption(value) { return dataScopeOptions.find(option => option.value === Number(value)) }

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

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ roleName: '', roleCode: '' })

const { syncListRoute, changePage, changePageSize } = useListRouteState({ query, page, reload: load, isLoading: () => loading.value })

const dsTypeChoices = computed(() => dataScopeOptions)

async function load() {
  const sequence = ++loadSequence
  const request = nextListRequest()
  loading.value = true
  try {
    const params = await syncListRoute()
    if (!params || sequence !== loadSequence) return
    const data = await api.list(params, request)
    if (sequence !== loadSequence) return
    rows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []
    total.value = Number(Array.isArray(data) ? data.length : data?.total ?? data?.totalCount ?? rows.value.length)

    loadError.value = ''
  } catch (error) {
    if (sequence === loadSequence && error?.code !== 'ERR_CANCELED') loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { query.roleName = ''; query.roleCode = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条角色吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.roleId)
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')
    if (rows.value.length === 1 && page.current > 1) page.current--
    await load()

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

const permissionRef = ref()
function openMenus(row) { if (canMenus.value) return permissionRef.value?.open(row) }
</script>

<style scoped>
.scope-error { color: #a94637; font-size: 13px; }
.scope-hint { margin: 8px 0 0; color: #737373; font-size: 13px; line-height: 1.6; }
.scope-hint.scope-error { color: #a94637; }
.menu-permission-hint { margin: 0 0 16px; color: #737373; font-size: 13px; }
</style>
