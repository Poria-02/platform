<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" :show-close="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑版本' : '新增版本'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :disabled="editor.saving" :validate-on-rule-change="false" scroll-to-error :scroll-into-view-options="{ block: 'center', inline: 'nearest' }" label-position="top" class="editor-form">
      <el-form-item label="应用" prop="appName">
        <el-select v-model="editor.form.appName" :disabled="editor.editing" style="width:100%" placeholder="请选择应用" @update:model-value="clearFieldError('appName')"><el-option v-for="option in appNameChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="系统" prop="appOs">
        <el-select v-model="editor.form.appOs" :disabled="editor.editing" style="width:100%" placeholder="请选择系统" @update:model-value="clearFieldError('appOs')"><el-option v-for="option in appOsChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="版本" prop="version">
        <el-input v-model.trim="editor.form.version" :disabled="editor.editing && editor.form.isNew === false" maxlength="64" placeholder="如 1.0.0，新版本需高于已有版本" @update:model-value="clearFieldError('version')" />
        <small v-if="editor.editing && editor.form.isNew === false">历史版本的版本号不可修改</small>
      </el-form-item>
      <el-form-item label="更新方式" prop="appUpdateType">
        <el-select v-model="editor.form.appUpdateType" style="width:100%" placeholder="请选择更新方式" @update:model-value="clearFieldError('appUpdateType')"><el-option v-for="option in appUpdateTypeChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="下载地址" prop="url">
        <el-input v-model.trim="editor.form.url" maxlength="255" placeholder="请输入安装包或应用商店下载地址" @update:model-value="clearFieldError('url')" />
      </el-form-item>
      <el-form-item label="更新提示" prop="prompt">
        <el-input v-model.trim="editor.form.prompt" maxlength="600" show-word-limit type="textarea" :rows="3" placeholder="请输入本次更新内容" @update:model-value="clearFieldError('prompt')" />
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
  appNameChoices: { type: Array, required: true },
  appOsChoices: { type: Array, required: true },
  appUpdateTypeChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({
  appName: [{ required: true, message: '请选择应用', trigger: 'change' }],
  appOs: [{ required: true, message: '请选择系统', trigger: 'change' }],
  version: [
    { required: true, message: '请填写版本', trigger: 'blur' },
    { pattern: /^[vV]?[0-9]+(?:\.[0-9]+)*$/, message: '版本号格式不正确，请输入如 1.0.0', trigger: 'blur' }
  ],
  appUpdateType: [{ required: true, message: '请选择更新方式', trigger: 'change' }],
  url: [{ required: true, whitespace: true, message: '请填写下载地址', trigger: 'blur' }, { max: 255, message: '下载地址不能超过 255 个字符', trigger: 'blur' }],
  prompt: [{ required: true, whitespace: true, message: '请填写更新提示', trigger: 'blur' }, { max: 600, message: '更新提示不能超过 600 个字符', trigger: 'blur' }]
}))

function showEditor(form, editing) {
  form.appName = normalizeSelectValue(form.appName, { dictionary: 'app_name' }, props.appNameChoices)
  form.appOs = normalizeSelectValue(form.appOs, { dictionary: 'app_os' }, props.appOsChoices)
  form.appUpdateType = normalizeSelectValue(form.appUpdateType, { dictionary: 'app_update_type' }, props.appUpdateTypeChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ appName: '', appOs: '', version: '', appUpdateType: '', url: '', prompt: '', ...defaults }, false)
}

async function openEdit(row) {
  if (!props.canEdit) return
  try {
    const detail = await api.detail(row.id)
    if (!detail?.id) throw new Error('版本不存在或已被删除')
    showEditor(detail, true)
  }
  catch (error) { if (!error?.response && !error?.config) ElMessage.error(error?.message || '编辑信息加载失败') }
}

async function save() {
  if (editor.saving || !(editor.editing ? props.canEdit : props.canCreate)) return
  editor.saving = true
  try {
    if (!(await formRef.value.validate().catch(() => false))) return
    const form = {
      ...(editor.editing ? { id: editor.form.id } : {}),
      appName: editor.form.appName,
      appOs: editor.form.appOs,
      version: editor.form.version,
      appUpdateType: editor.form.appUpdateType,
      url: editor.form.url,
      prompt: editor.form.prompt
    }

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
