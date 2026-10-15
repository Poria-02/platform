<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑字典' : '新增字典'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item v-if="!editor.editing" label="编码" prop="code">
        <el-input v-model="editor.form.code" placeholder="请输入编码" @update:model-value="clearFieldError('code')" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="editor.form.name" placeholder="请输入名称" @update:model-value="clearFieldError('name')" />
      </el-form-item>
      <el-form-item v-if="!editor.editing" label="树形结构" prop="isTree">
        <el-select v-model="editor.form.isTree" style="width:100%" placeholder="请选择树形结构" @update:model-value="clearFieldError('isTree')"><el-option v-for="option in isTreeChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="editor.form.remark" type="textarea" :rows="3" placeholder="请输入备注" @update:model-value="clearFieldError('remark')" />
      </el-form-item>

    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button :disabled="!(editor.editing ? canEdit : canCreate)" type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { normalizeSelectValue } from '@/core/resourceValues'
import * as api from './api'

const props = defineProps({
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  isTreeChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ code: !editor.editing ? [{ required: true, message: "请填写编码", trigger: 'submit' }] : [], name: [{ required: true, message: "请填写名称", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.isTree = normalizeSelectValue(form.isTree, {  }, props.isTreeChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ code: '', name: '', isTree: true, remark: '', ...defaults }, false)
}

async function openEdit(row) {
  if (!props.canEdit) return
  try { showEditor({ ...row }, true) }
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

<style scoped>.item-location { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; color: #808080; font-size: 12px; }</style>
