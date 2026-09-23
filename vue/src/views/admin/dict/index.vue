<template>
  <ResourcePage title="字典管理" section="系统管理" description="维护字典类型与字典项。" singular="字典" :api="api" :columns="columns" :filters="filters" :fields="fields">
    <template #row-actions="{ row }"><el-button link type="primary" @click="selected = row">字典项</el-button></template>
  </ResourcePage>
  <el-dialog :model-value="Boolean(selected)" :title="`字典项 · ${selected?.type || ''}`" width="min(980px, calc(100vw - 24px))" @close="selected = null">
    <ResourcePage v-if="selected" title="字典项" section="字典管理" :api="itemApi" :columns="itemColumns" :fields="itemFields" />
  </el-dialog>
</template>
<script setup>
import { computed, ref } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const selected = ref(null)
const columns = [{ prop: 'type', label: '字典类型' }, { prop: 'description', label: '说明' }, { prop: 'system', label: '所属系统', dictionary: 'dict_type' }, { prop: 'remarks', label: '备注' }]
const filters = [{ prop: 'type', label: '字典类型' }, { prop: 'system', label: '所属系统', type: 'select', dictionary: 'dict_type' }]
const fields = [{ prop: 'type', label: '字典类型', required: true }, { prop: 'description', label: '说明' }, { prop: 'system', label: '所属系统', type: 'select', dictionary: 'dict_type', default: '0' }, { prop: 'remarks', label: '备注', type: 'textarea' }]
const itemColumns = [{ prop: 'label', label: '显示名称' }, { prop: 'value', label: '值' }, { prop: 'sort', label: '排序' }, { prop: 'description', label: '说明' }]
const itemFields = [{ prop: 'label', label: '显示名称', required: true }, { prop: 'value', label: '值', required: true }, { prop: 'sort', label: '排序', type: 'number', default: 0 }, { prop: 'description', label: '说明' }]
const itemApi = computed(() => ({
  list: async ({ current = 1, size = 10 } = {}) => {
    const data = await api.listItems({ dictId: selected.value.id, current: 1, size: 1000 })
    const rows = [...(data.records || [])].sort((a, b) => Number(a.sort) - Number(b.sort) || String(a.id).localeCompare(String(b.id), 'zh-CN', { numeric: true }))
    return { records: rows.slice((current - 1) * size, current * size), total: rows.length }
  },
  create: data => api.createItem({ ...data, dictId: selected.value.id, type: selected.value.type }),
  update: api.updateItem,
  remove: api.removeItem
}))
</script>
