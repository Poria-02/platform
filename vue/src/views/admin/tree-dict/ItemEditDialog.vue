<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!itemEditor.saving" :close-on-press-escape="!itemEditor.saving" v-model="itemEditor.open" :title="itemEditor.editing ? '编辑字典项' : '新增字典项'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="itemFormRef" :model="itemEditor.form" :rules="itemFormRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="名称" prop="name">
        <el-input v-model="itemEditor.form.name" placeholder="请输入名称" @update:model-value="clearItemFieldError('name')" />
      </el-form-item>
      <el-form-item label="值" prop="value">
        <el-input v-model="itemEditor.form.value" placeholder="请输入值" @update:model-value="clearItemFieldError('value')" />
      </el-form-item>
      <el-form-item label="上级节点 ID" prop="pid">
        <el-input v-model="itemEditor.form.pid" :disabled="itemEditor.editing" placeholder="请输入上级节点 ID" @update:model-value="clearItemFieldError('pid')" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="itemEditor.form.sort" :min="0" controls-position="right" style="width:100%" @update:model-value="clearItemFieldError('sort')" />
      </el-form-item>
      <el-form-item label="简称" prop="simpleName">
        <el-input v-model="itemEditor.form.simpleName" placeholder="请输入简称" @update:model-value="clearItemFieldError('simpleName')" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="itemEditor.form.remark" placeholder="请输入备注" @update:model-value="clearItemFieldError('remark')" />
      </el-form-item>

    </el-form>
    <template #footer><el-button :disabled="itemEditor.saving" @click="itemEditor.open = false">取消</el-button><el-button type="primary" v-stable-loading="itemEditor.saving" :loading="itemEditor.saving" @click="saveItem">保存</el-button></template>
  </el-dialog>
</template>

<script setup>

import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import * as api from './api'

const props = defineProps({
  selected: { type: Object, required: true },
  parentId: { type: [String, Number], required: true },
  itemCanCreate: { type: Boolean, required: true },
  itemCanEdit: { type: Boolean, required: true }
})
const emit = defineEmits(["saved"])

const itemEditor = reactive({ open: false, editing: false, saving: false, form: {} })

const itemFormRef = ref()

const itemFormRules = computed(() => ({ name: [{ required: true, message: "请填写名称", trigger: 'submit' }], value: [{ required: true, message: "请填写值", trigger: 'submit' }] }))

function showItemEditor(form, editing) {

  itemEditor.editing = editing
  itemEditor.form = form
  itemEditor.open = true
  nextTick(() => itemFormRef.value?.clearValidate())
}

function clearItemFieldError(field) { itemFormRef.value?.clearValidate(field) }

function openCreateItem(defaults = {}) {
  if (!props.itemCanCreate) return
  showItemEditor({ name: '', value: '', pid: props.parentId, sort: 0, simpleName: '', remark: '', ...defaults }, false)
}

async function openEditItem(row) {
  if (!props.itemCanEdit) return
  try { showItemEditor({ ...row }, true) }
  catch (error) { if (!error?.response && !error?.config) ElMessage.error(error?.message || '编辑信息加载失败') }
}

async function saveItem() {
  if (!(itemEditor.editing ? props.itemCanEdit : props.itemCanCreate)) return
  if (!(await itemFormRef.value.validate().catch(() => false))) return
  itemEditor.saving = true
  try {
    const form = { ...itemEditor.form }

    const result = await (itemEditor.editing ? api.updateItem({ ...form, dictId: props.selected.id }) : api.createItem({ ...form, pid: form.pid || props.parentId, dictId: props.selected.id }))
    if (result === false) { ElMessage.error('保存未完成'); return }
    ElMessage.success('保存成功')
    itemEditor.open = false
    emit('saved')

  } catch {
    // 请求层显示错误，保留填写内容。
  } finally { itemEditor.saving = false }
}

defineExpose({ openCreate: openCreateItem, openEdit: openEditItem, close: () => { itemEditor.open = false } })

</script>

<style scoped>.item-location { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; color: #808080; font-size: 12px; }</style>
