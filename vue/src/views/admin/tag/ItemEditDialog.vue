<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!itemEditor.saving" :close-on-press-escape="!itemEditor.saving" v-model="itemEditor.open" :title="itemEditor.editing ? '编辑标签项' : '新增标签项'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="itemFormRef" :model="itemEditor.form" :rules="itemFormRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="名称" prop="name">
        <el-input v-model="itemEditor.form.name" placeholder="请输入名称" @update:model-value="clearItemFieldError('name')" />
      </el-form-item>
      <el-form-item label="值" prop="value">
        <el-input v-model="itemEditor.form.value" placeholder="请输入值" @update:model-value="clearItemFieldError('value')" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="itemEditor.form.status" style="width:100%" placeholder="请选择状态" @update:model-value="clearItemFieldError('status')"><el-option v-for="option in itemStatusChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="itemEditor.form.remark" placeholder="请输入备注" @update:model-value="clearItemFieldError('remark')" />
      </el-form-item>

    </el-form>
    <template #footer><el-button :disabled="itemEditor.saving" @click="itemEditor.open = false">取消</el-button><el-button type="primary" :loading="itemEditor.saving" @click="saveItem">保存</el-button></template>
  </el-dialog>
</template>

<script setup>

import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { normalizeSelectValue } from '@/core/resourceValues'
import * as api from './api'

const props = defineProps({
  selected: { type: Object, required: true },
  itemCanCreate: { type: Boolean, required: true },
  itemCanEdit: { type: Boolean, required: true },
  itemStatusChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const itemEditor = reactive({ open: false, editing: false, saving: false, form: {} })

const itemFormRef = ref()

const itemFormRules = computed(() => ({ name: [{ required: true, message: "请填写名称", trigger: 'submit' }], value: [{ required: true, message: "请填写值", trigger: 'submit' }] }))

function showItemEditor(form, editing) {
  form.status = normalizeSelectValue(form.status, {  }, props.itemStatusChoices)
  itemEditor.editing = editing
  itemEditor.form = form
  itemEditor.open = true
  nextTick(() => itemFormRef.value?.clearValidate())
}

function clearItemFieldError(field) { itemFormRef.value?.clearValidate(field) }

function openCreateItem(defaults = {}) {
  if (!props.itemCanCreate) return
  showItemEditor({ name: '', value: '', status: 1, remark: '', ...defaults }, false)
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

    const result = await (itemEditor.editing ? api.updateItem(form) : api.createItem({ ...form, tagId: props.selected.id }))
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
