<template>
  <ResourcePage title="标签管理" section="系统管理" description="维护标签和标签项。" singular="标签" :api="api" :columns="columns" :filters="filters" :fields="fields">
    <template #row-actions="{ row }"><el-button link type="primary" @click="selected = row">标签项</el-button></template>
  </ResourcePage>
  <el-dialog :model-value="Boolean(selected)" :title="`标签项 · ${selected?.name || ''}`" width="min(980px, calc(100vw - 24px))" @close="selected = null">
    <ResourcePage v-if="selected" title="标签项" section="标签管理" :api="itemApi" :columns="itemColumns" :fields="itemFields" />
  </el-dialog>
</template>
<script setup>
import { computed, ref } from 'vue'
import ResourcePage from '@/components/ResourcePage.vue'
import * as api from './api'
const selected = ref(null)
const columns = [{ prop: 'tagKey', label: '标签键' }, { prop: 'name', label: '名称' }, { prop: 'createTime', label: '创建时间' }]
const filters = [{ prop: 'tagKey', label: '标签键' }, { prop: 'name', label: '名称' }]
const fields = [{ prop: 'tagKey', label: '标签键', required: true }, { prop: 'name', label: '名称', required: true }]
const itemColumns = [{ prop: 'name', label: '名称' }, { prop: 'value', label: '值' }, { prop: 'status', label: '状态' }, { prop: 'remark', label: '备注' }]
const itemFields = [{ prop: 'name', label: '名称', required: true }, { prop: 'value', label: '值', required: true }, { prop: 'status', label: '状态', type: 'number' }, { prop: 'remark', label: '备注' }]
const itemApi = computed(() => ({
  list: params => api.listItems({ ...params, tagId: selected.value.id }),
  create: data => api.createItem({ ...data, tagId: selected.value.id }),
  update: api.updateItem,
  remove: api.removeItem
}))
</script>
