<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑参数' : '新增参数'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="参数键" prop="publicKey">
        <el-input v-model="editor.form.publicKey" placeholder="请输入参数键" @update:model-value="clearFieldError('publicKey')" />
      </el-form-item>
      <el-form-item label="参数值" prop="publicValue">
        <el-input v-model="editor.form.publicValue" placeholder="请输入参数值" @update:model-value="clearFieldError('publicValue')" />
      </el-form-item>
      <el-form-item label="名称" prop="publicName">
        <el-input v-model="editor.form.publicName" placeholder="请输入名称" @update:model-value="clearFieldError('publicName')" />
      </el-form-item>
      <el-form-item label="参数类型" prop="publicType">
        <el-select v-model="editor.form.publicType" style="width:100%" placeholder="请选择参数类型" @update:model-value="clearFieldError('publicType')"><el-option v-for="option in publicTypeChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="editor.form.status" style="width:100%" placeholder="请选择状态" @update:model-value="clearFieldError('status')"><el-option v-for="option in statusChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input v-model="editor.form.remarks" maxlength="500" show-word-limit type="textarea" :rows="3" placeholder="请输入备注" @update:model-value="clearFieldError('remarks')" />
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
  publicTypeChoices: { type: Array, required: true },
  statusChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const prepareEdit = row => ({ ...row, status: String(row.status) === '0' ? '2' : row.status })

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ publicKey: [{ required: true, message: "请填写参数键", trigger: 'submit' }], publicName: [{ required: true, message: "请填写名称", trigger: 'submit' }], publicValue: [{ required: true, message: "请填写参数值", trigger: 'submit' }], status: [{ required: true, message: "请填写状态", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.publicType = normalizeSelectValue(form.publicType, { dictionary: 'param_type' }, props.publicTypeChoices)
  form.status = normalizeSelectValue(form.status, {  }, props.statusChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ publicKey: '', publicValue: '', publicName: '', publicType: '0', status: '1', remarks: '', ...defaults }, false)
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
