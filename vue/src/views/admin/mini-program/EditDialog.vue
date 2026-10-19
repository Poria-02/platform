<template>
  <el-dialog v-model="editor.open" class="business-dialog" :title="editor.editing ? '编辑微信账号' : '新增微信账号'" width="min(680px, calc(100vw - 32px))" append-to-body :show-close="!editor.saving" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving">
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" :disabled="editor.saving" scroll-to-error :scroll-into-view-options="{ block: 'center', inline: 'nearest' }" label-position="top" class="editor-form">
      <el-form-item label="类别" prop="type"><el-select v-model="editor.form.type" style="width:100%"><el-option label="公众号" value="WX" /><el-option label="小程序" value="MINI" /></el-select></el-form-item>
      <el-form-item label="账号名称" prop="remark"><el-input v-model="editor.form.remark" placeholder="请输入便于识别的账号名称" /></el-form-item>
      <el-form-item label="AppId" prop="appId"><el-input v-model="editor.form.appId" placeholder="请输入微信分配的 AppId" @update:model-value="clearFieldError('appId')" /></el-form-item>
      <el-form-item label="AppSecret" prop="appSecret"><el-input v-model="editor.form.appSecret" type="password" show-password autocomplete="new-password" :placeholder="editor.editing ? '留空保留原有密钥' : '请输入 AppSecret'" @update:model-value="clearFieldError('appSecret')" /></el-form-item>
      <el-form-item label="回调地址" prop="redirectUrl" class="form-field--wide"><el-input v-model="editor.form.redirectUrl" placeholder="请输入回调地址（可选）" /></el-form-item>
    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button :disabled="!(editor.editing ? canEdit : canCreate)" type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { showRequestError } from '@/core/http'
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as api from './api'

const props = defineProps({ canCreate: { type: Boolean, required: true }, canEdit: { type: Boolean, required: true } })
const emit = defineEmits(['saved'])
const editor = reactive({ open: false, editing: false, saving: false, form: {} })
const formRef = ref()
const formRules = computed(() => ({
  type: [{ required: true, message: '请选择类别', trigger: 'change' }],
  appId: [{ required: true, whitespace: true, message: '请填写 AppId', trigger: 'blur' }],
  appSecret: [{ required: !editor.editing, whitespace: true, message: '请填写 AppSecret', trigger: 'blur' }]
}))

function clearFieldError(field) { formRef.value?.clearValidate(field) }
function showEditor(row, editing) {
  editor.form = { id: row.id, type: row.type || 'MINI', remark: row.remark || '', appId: row.appId || '', appSecret: '', redirectUrl: row.redirectUrl || '' }
  editor.editing = editing
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}
function openCreate() { if (props.canCreate && !editor.saving) showEditor({}, false) }
function openEdit(row) { if (props.canEdit && !editor.saving) showEditor(row, true) }

async function save() {
  if (editor.saving || !(editor.editing ? props.canEdit : props.canCreate)) return
  editor.saving = true
  try {
    if (!await formRef.value.validate().catch(() => false)) return
    const data = { type: editor.form.type, appId: editor.form.appId.trim(), appSecret: editor.form.appSecret, remark: editor.form.remark.trim(), redirectUrl: editor.form.redirectUrl.trim() }
    if (editor.editing) data.id = editor.form.id
    const result = await (editor.editing ? api.update(data) : api.create(data))
    if (result === false) { ElMessage.error('保存未完成'); return }
    editor.form.appSecret = ''
    ElMessage.success('保存成功')
    editor.open = false
    emit('saved')
  } catch (error) { showRequestError(error, '微信账号保存失败') }
  finally { editor.saving = false }
}

defineExpose({ openCreate, openEdit })
</script>
