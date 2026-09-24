<template>
  <ResourcePage title="分级字典" section="系统管理" description="维护层级字典与字典项。" singular="字典" :api="api" :columns="columns" :filters="filters" :fields="fields">
    <template #row-actions="{ row }"><el-button link type="primary" @click="selectDict(row)">字典项</el-button></template>
  </ResourcePage>
  <el-dialog :model-value="Boolean(selected)" :title="`字典项 · ${selected?.name || ''}`" width="min(980px, calc(100vw - 24px))" @close="selected = null">
    <div v-if="itemParents.length" class="item-location"><el-button link type="primary" @click="itemParents.pop()">返回上级</el-button><span>{{ itemParents.map(item => item.name).join(' / ') }}</span></div>
    <ResourcePage v-if="selected" :key="`${selected.id}:${parentId}`" title="字典项" section="分级字典" :api="itemApi" :columns="itemColumns" :fields="itemFields" :pagination="false">
      <template #row-actions="{ row }"><el-button link type="primary" @click="itemParents.push({ id: row.id, name: row.name })">查看子项</el-button></template>
    </ResourcePage>
  </el-dialog>
</template>
<script setup>
import { computed, ref } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const selected = ref(null)
const itemParents = ref([])
const parentId = computed(() => itemParents.value.at(-1)?.id || '0')
function selectDict(row) { selected.value = row; itemParents.value = [] }
const columns = [{ prop: 'code', label: '编码' }, { prop: 'name', label: '名称' }, { prop: 'remark', label: '备注', width: 220 }, { prop: 'createTime', label: '创建时间', width: 180 }]
const filters = [{ prop: 'code', label: '编码' }, { prop: 'name', label: '名称' }]
const fields = [{ prop: 'code', label: '编码', required: true, createOnly: true }, { prop: 'name', label: '名称', required: true }, { prop: 'isTree', label: '树形结构', type: 'select', createOnly: true, default: true, options: [{ label: '是', value: true }, { label: '否', value: false }] }, { prop: 'remark', label: '备注', type: 'textarea' }]
const itemColumns = [{ prop: 'name', label: '名称' }, { prop: 'value', label: '值' }, { prop: 'pid', label: '上级节点 ID' }, { prop: 'sort', label: '排序' }, { prop: 'remark', label: '备注' }]
const itemFields = computed(() => [{ prop: 'name', label: '名称', required: true }, { prop: 'value', label: '值', required: true }, { prop: 'pid', label: '上级节点 ID', default: parentId.value, immutable: true }, { prop: 'sort', label: '排序', type: 'number', default: 0 }, { prop: 'simpleName', label: '简称' }, { prop: 'remark', label: '备注' }])
const itemApi = computed(() => ({
  list: () => api.listItems(selected.value.code, parentId.value),
  create: data => api.createItem({ ...data, pid: data.pid || parentId.value, dictId: selected.value.id }),
  update: data => api.updateItem({ ...data, dictId: selected.value.id }),
  remove: api.removeItem
}))
</script>
<style scoped>.item-location { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; color: #6b8490; font-size: 12px; }</style>
