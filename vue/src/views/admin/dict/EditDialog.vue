<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑字典' : '新增字典'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="字典类型" prop="type">
        <el-input v-model="editor.form.type" placeholder="请输入字典类型" @update:model-value="clearFieldError('type')" />
      </el-form-item>
      <el-form-item label="说明" prop="description">
        <el-input v-model="editor.form.description" placeholder="请输入说明" @update:model-value="clearFieldError('description')" />
      </el-form-item>
      <el-form-item label="所属系统" prop="system">
        <el-select v-model="editor.form.system" style="width:100%" placeholder="请选择所属系统" @update:model-value="clearFieldError('system')"><el-option v-for="option in systemChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input v-model="editor.form.remarks" type="textarea" :rows="3" placeholder="请输入备注" @update:model-value="clearFieldError('remarks')" />
      </el-form-item>

    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">保存</el-button></template>
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
  systemChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ type: [{ required: true, message: "请填写字典类型", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.system = normalizeSelectValue(form.system, { dictionary: 'dict_type' }, props.systemChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ type: '', description: '', system: '0', remarks: '', ...defaults }, false)
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
