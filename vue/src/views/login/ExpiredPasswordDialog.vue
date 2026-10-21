<template>
  <el-dialog v-model="editor.open" class="business-dialog" title="密码已过期，请修改密码" width="min(480px, calc(100vw - 28px))" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" :show-close="!editor.saving" append-to-body @closed="clearSecrets">
    <el-form ref="formRef" :model="editor.form" :rules="rules" :disabled="editor.saving" scroll-to-error :scroll-into-view-options="{ block: 'center', inline: 'nearest' }" label-position="top" :validate-on-rule-change="false" @submit.prevent="save" @keyup.enter="save">
      <p class="password-hint">新密码需要包含大小写字母、数字和特殊符号，长度为 8–20 位。修改成功后请使用新密码重新登录。</p>
      <el-form-item label="用户名"><el-input :model-value="editor.username" autocomplete="username" readonly /></el-form-item>
      <el-form-item label="原密码" prop="password"><el-input v-model="editor.form.password" type="password" autocomplete="current-password" show-password placeholder="请输入原密码" /></el-form-item>
      <el-form-item label="新密码" prop="newpassword1"><el-input v-model="editor.form.newpassword1" type="password" autocomplete="new-password" show-password maxlength="20" placeholder="请输入新密码" /></el-form-item>
      <el-form-item label="确认新密码" prop="confirmation"><el-input v-model="editor.form.confirmation" type="password" autocomplete="new-password" show-password maxlength="20" placeholder="请再次输入新密码" /></el-form-item>
    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">修改密码</el-button></template>
  </el-dialog>
</template>

<script setup>
import { showRequestError } from '@/core/http'
import { onBeforeUnmount, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { updateExpiredPassword } from './api'

const emit = defineEmits(['saved'])
const formRef = ref()
const editor = reactive({ open: false, username: '', saving: false, form: { password: '', newpassword1: '', confirmation: '' } })
const rules = {
  password: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newpassword1: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{8,20}$/, message: '密码需包含大小写字母、数字和特殊符号，长度8–20位', trigger: 'blur' },
    { validator: (_rule, value, callback) => callback(value === editor.form.password ? new Error('新密码不能与原密码相同') : undefined), trigger: 'blur' }
  ],
  confirmation: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: (_rule, value, callback) => callback(value !== editor.form.newpassword1 ? new Error('两次输入的密码不一致') : undefined), trigger: 'blur' }]
}

function clearSecrets() {
  Object.assign(editor.form, { password: '', newpassword1: '', confirmation: '' })
  formRef.value?.clearValidate()
}
function open(username) {
  if (editor.saving) return
  clearSecrets()
  editor.username = username
  editor.open = true
}
async function save() {
  if (editor.saving) return
  editor.saving = true
  try {
    if (!await formRef.value?.validate().catch(() => false)) return
    const result = await updateExpiredPassword({ username: editor.username, password: editor.form.password, newpassword1: editor.form.newpassword1 })
    if (result !== true) { ElMessage.error('密码修改未完成，请稍后重试'); return }
    ElMessage.success('密码已修改，请使用新密码重新登录')
    editor.open = false
    clearSecrets()
    emit('saved', editor.username)
  } catch (error) {
    showRequestError(error, '密码修改失败')
  } finally { editor.saving = false }
}
defineExpose({ open })
onBeforeUnmount(clearSecrets)
</script>

<style scoped>
.password-hint { margin: 0 0 22px; color: var(--muted); font-size: 13px; line-height: 1.8; }
.el-form-item { margin-bottom: 24px; }
:deep(.el-form-item__label) { margin-bottom: 8px; font-size: 13px; }
:deep(.el-input__wrapper) { min-height: 42px; }
</style>
