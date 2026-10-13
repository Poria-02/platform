<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / 组织与权限</div><h1>用户管理</h1><p>维护平台账号、所属部门和登录状态。</p></div>
    </header>
    <div class="resource-toolbar">
      <div class="filter-bar">
        <el-input v-model="query.username" placeholder="搜索用户名" clearable @keyup.enter="search" />
        <el-input v-model="query.phone" placeholder="搜索手机号" clearable @keyup.enter="search" />
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button text @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions">

        <el-button @click="load">刷新</el-button>
        <el-button v-if="canCreate" type="primary" @click="openCreate()">新增用户</el-button>
      </div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>
      <div v-if="dictionaryError" class="resource-error" role="alert"><span>{{ dictionaryError }}</span><el-button link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="userId" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column prop="username" label="用户名" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.username, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.phone, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="deptName" label="部门" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.deptName, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="lockFlag" label="状态" :min-width="130" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.lockFlag, { dictionary: 'status_type' }, lockFlagChoices, scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" :min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ displayResourceValue(scope.row.createTime, {}, [], scope.row) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="190" fixed="right">
          <template #default="scope">

            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination v-model:current-page="page.current" v-model:page-size="page.size" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @size-change="load" @current-change="load" />
      </div>
    </section>
    <EditDialog ref="editorRef" :departments="departments" :department-error="departmentError" :load-departments="loadDepartments" :can-create="canCreate" :can-edit="canEdit" :lock-flag-choices="lockFlagChoices" :role-choices="roleChoices" @saved="load" />
  </div>
</template>

<script setup>
import EditDialog from './EditDialog.vue'

import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { displayResourceValue, optionsForField } from '@/core/resourceValues'
import { tree as departmentTree } from '@/views/admin/dept/api'
import * as api from './api'
import { http } from '@/core/http'

const roles = ref([])

const departments = ref([])

const departmentError = ref('')

let departmentsLoading = false

const lockOptions = [{ label: '正常', value: '0' }, { label: '冻结', value: '9' }]

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

const canCreate = computed(() => Boolean(api.create))

const canEdit = computed(() => Boolean(api.update))

const canRemove = computed(() => Boolean(api.remove))

const loading = ref(false)
const loadError = ref('')
const rows = ref([])
let loadSequence = 0

const total = ref(0)
const page = reactive({ current: 1, size: 10 })

const query = reactive({ username: '', phone: '' })

const dictionaryItems = ref([])
const dictionaryError = ref('')

const lockFlagChoices = computed(() => optionsForField({ dictionary: 'status_type', options: lockOptions }, dictionaryItems.value))

const roleChoices = computed(() => roles.value.map(role => ({ label: role.roleName, value: role.roleId })))

async function loadDictionaries() {
  const types = ["status_type"]
  const results = await Promise.allSettled(types.map(async type => {
    const data = await http.get(`/upms/dict/type/${encodeURIComponent(type)}`, { silent: true })
    if (!Array.isArray(data)) throw new Error('字典数据格式错误')
    return data.map(entry => ({ ...entry, type }))
  }))
  dictionaryItems.value = results.flatMap((result, i) => result.status === 'fulfilled'
    ? result.value : dictionaryItems.value.filter(entry => entry.type === types[i]))
  dictionaryError.value = results.some(result => result.status === 'rejected') ? '部分选项加载失败，请重试' : ''
}

async function load() {
  const sequence = ++loadSequence
  loading.value = true
  try {
    const params = { ...Object.fromEntries(Object.entries(query).filter(([, value]) => value !== '' && value !== null && value !== undefined)), current: page.current, size: page.size }
    const [data] = await Promise.all([api.list(params), loadDictionaries()])
    if (sequence !== loadSequence) return
    rows.value = Array.isArray(data) ? data : data?.records || data?.rows || data?.list || []
    total.value = Number(Array.isArray(data) ? data.length : data?.total ?? data?.totalCount ?? rows.value.length)

    loadError.value = ''
  } catch (error) {
    if (sequence === loadSequence) loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { query.username = ''; query.phone = ''; return search() }

async function remove(row) {
  if (!canRemove.value) return
  try { await ElMessageBox.confirm("确认删除这条用户吗？", '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await api.remove(row.userId)
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
</script>

<style scoped>.department-error { color: #a94637; font-size: 13px; }</style>
