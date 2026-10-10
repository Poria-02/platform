<template>
  <ResourcePage ref="pageRef" title="部门管理" section="组织与权限" description="维护组织层级、部门信息及上下级关系。" singular="部门" :api="pageApi" id-key="id" :columns="columns" :filters="filters" :fields="fields" :pagination="false" tree-mode :permissions="permissions" :prepare-edit="prepareEdit" :remove-message="removeMessage">
    <template #actions><el-button @click="pageRef.expandAll()">展开全部</el-button><el-button @click="pageRef.collapseAll()">收起全部</el-button></template>
    <template #row-actions="{ row }"><el-button v-if="session.hasPermission(permissions.create)" link type="primary" @click="pageRef.openCreate({ parentId: row.id })">新增下级</el-button></template>
    <template #field-parentId="{ form }">
      <el-tree-select v-model="form.parentId" :data="parentOptions(form.deptId)" :props="{ label: 'name', children: 'children' }" node-key="id" check-strictly filterable default-expand-all style="width:100%" placeholder="请选择上级部门" />
    </template>
  </ResourcePage>
</template>

<script setup>
import { ref } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import { useSession } from '@/core/session'
import * as api from './api'

const session = useSession()
const pageRef = ref()
const departments = ref([])
const permissions = { create: 'sys_dept_add', update: 'sys_dept_edit', remove: 'sys_dept_del' }
const columns = [{ prop: 'name', label: '部门名称', width: 250 }, { prop: 'id', label: '部门编号', width: 120 }]
const filters = [{ prop: 'name', label: '部门名称' }]
const fields = [
  { prop: 'name', label: '部门名称', required: true },
  { prop: 'parentId', label: '上级部门', required: true, default: 0 },
  { prop: 'type', label: '部门类型', type: 'select', required: true, default: 1, options: [{ label: '机构', value: 1 }, { label: '科室', value: 2 }] },
  { prop: 'sort', label: '排序', type: 'number', default: 0, required: true },
  { prop: 'detailId', label: '关联编号', placeholder: '选填' }
]

const pageApi = {
  ...api,
  async list({ name = '' } = {}) {
    departments.value = await api.tree() || []
    const keyword = name.trim()
    function filter(nodes) {
      return nodes.flatMap(node => {
        const children = filter(node.children || [])
        return node.name?.includes(keyword) ? [node] : children.length ? [{ ...node, children }] : []
      })
    }
    return keyword ? filter(departments.value) : departments.value
  }
}

function parentOptions(deptId) {
  function exclude(nodes) {
    return nodes.filter(node => String(node.id) !== String(deptId)).map(node => ({ ...node, children: exclude(node.children || []) }))
  }
  return [{ id: 0, name: '顶级部门', children: exclude(departments.value) }]
}

async function prepareEdit(row) {
  const dept = await api.detail(row.id)
  if (!dept) throw new Error('部门不存在，请刷新列表')
  return { ...dept, parentId: dept.parentId ?? 0 }
}

function removeMessage(row) {
  return `确认删除“${row.name}”及其所有下级部门吗？请先处理关联的用户。`
}
</script>
