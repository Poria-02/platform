<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑部门' : '新增部门'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="editor.form.name" placeholder="请输入部门名称" @update:model-value="clearFieldError('name')" />
      </el-form-item>
      <el-form-item label="上级部门" prop="parentId">
        <el-tree-select v-model="editor.form.parentId" :data="parentOptions(editor.form.deptId)" :props="{ label: 'name', children: 'children' }" node-key="id" check-strictly filterable default-expand-all style="width:100%" placeholder="请选择上级部门" />
      </el-form-item>
      <el-form-item label="部门类型" prop="type">
        <el-select v-model="editor.form.type" style="width:100%" placeholder="请选择部门类型" @update:model-value="clearFieldError('type')"><el-option v-for="option in typeChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="editor.form.sort" :min="0" controls-position="right" style="width:100%" @update:model-value="clearFieldError('sort')" />
      </el-form-item>
      <el-form-item label="关联编号" prop="detailId">
        <el-input v-model="editor.form.detailId" placeholder="选填" @update:model-value="clearFieldError('detailId')" />
      </el-form-item>

    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button type="primary" :loading="editor.saving" @click="save">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { normalizeSelectValue } from '@/core/resourceValues'

import * as api from './api'

const props = defineProps({
  parentOptions: { type: Function, required: true },
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  typeChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

async function prepareEdit(row) {
  const dept = await api.detail(row.id)
  if (!dept) throw new Error('部门不存在，请刷新列表')
  return { ...dept, parentId: dept.parentId ?? 0 }
}

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ name: [{ required: true, message: "请填写部门名称", trigger: 'submit' }], parentId: [{ required: true, message: "请填写上级部门", trigger: 'submit' }], type: [{ required: true, message: "请填写部门类型", trigger: 'submit' }], sort: [{ required: true, message: "请填写排序", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.type = normalizeSelectValue(form.type, { dictionary: 'dept_type' }, props.typeChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ name: '', parentId: 0, type: props.typeChoices[0]?.value ?? '', sort: 0, detailId: '', ...defaults }, false)
}

async function openEdit(row) {
  if (!props.canEdit) return
  try { showEditor(await prepareEdit(row), true) }
  catch (error) { if (!error?.response && !error?.config) ElMessage.error(error?.message || '编辑信息加载失败') }
}

async function save() {
  if (!(editor.editing ? props.canEdit : props.canCreate)) return
  if (!(await formRef.value.validate().catch(() => false))) return
  editor.saving = true
  try {
    const form = { ...editor.form }

    const result = await (editor.editing ? api.update(form) : api.create(form))
    if (result === false) { ElMessage.error('保存未完成'); return }
    ElMessage.success('保存成功')
    editor.open = false
    emit('saved')

  } catch {
    // 请求层显示错误，保留填写内容。
  } finally { editor.saving = false }
}

defineExpose({ openCreate, openEdit })

</script>
