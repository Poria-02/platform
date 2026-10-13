<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑终端' : '新增终端'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="客户端 ID" prop="clientId">
        <el-input v-model="editor.form.clientId" :disabled="editor.editing" placeholder="请输入客户端 ID" @update:model-value="clearFieldError('clientId')" />
      </el-form-item>
      <el-form-item label="客户端密钥" prop="clientSecret">
        <el-input v-model="editor.form.clientSecret" type="password" show-password placeholder="请输入客户端密钥" @update:model-value="clearFieldError('clientSecret')" />
      </el-form-item>
      <el-form-item label="授权范围" prop="scope">
        <el-input v-model="editor.form.scope" placeholder="请输入授权范围" @update:model-value="clearFieldError('scope')" />
      </el-form-item>
      <el-form-item label="授权方式" prop="authorizedGrantTypes">
        <el-select v-model="editor.form.authorizedGrantTypes" multiple style="width:100%" placeholder="请选择授权方式" @update:model-value="clearFieldError('authorizedGrantTypes')"><el-option v-for="option in authorizedGrantTypesChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="客户端扩展配置 JSON" prop="additionalInformation">
        <el-input v-model="editor.form.additionalInformation" type="textarea" :rows="3" placeholder="{&quot;allowedLoginTypes&quot;:[&quot;ADMIN_PWD&quot;]}" @update:model-value="clearFieldError('additionalInformation')" />
      </el-form-item>
      <el-form-item label="令牌有效期" prop="accessTokenValidity">
        <el-input-number v-model="editor.form.accessTokenValidity" :min="0" controls-position="right" style="width:100%" @update:model-value="clearFieldError('accessTokenValidity')" />
      </el-form-item>
      <el-form-item label="刷新有效期" prop="refreshTokenValidity">
        <el-input-number v-model="editor.form.refreshTokenValidity" :min="0" controls-position="right" style="width:100%" @update:model-value="clearFieldError('refreshTokenValidity')" />
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
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  authorizedGrantTypesChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ clientId: [{ required: true, message: "请填写客户端 ID", trigger: 'submit' }], clientSecret: [{ required: true, message: "请填写客户端密钥", trigger: 'submit' }], scope: [{ required: true, message: "请填写授权范围", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.authorizedGrantTypes = normalizeSelectValue(form.authorizedGrantTypes, { dictionary: 'grant_types', multiple: true, joinWith: ',' }, props.authorizedGrantTypesChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ clientId: '', clientSecret: '', scope: '', authorizedGrantTypes: [], additionalInformation: '', accessTokenValidity: 0, refreshTokenValidity: 0, ...defaults }, false)
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
    if (Array.isArray(form.authorizedGrantTypes)) form.authorizedGrantTypes = form.authorizedGrantTypes.join(',')
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
