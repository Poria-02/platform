<template>
  <el-dialog v-model="editor.open" class="business-dialog" :title="editor.mode === 'phone' ? '修改手机号' : '修改密码'" width="min(480px, calc(100vw - 28px))" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" :show-close="!editor.saving" append-to-body @closed="clearSecrets">
    <el-form ref="formRef" :model="editor.form" :rules="rules" label-position="top" :validate-on-rule-change="false" @submit.prevent="save">
      <template v-if="editor.mode === 'phone'">
        <p class="profile-edit-hint">当前手机号：{{ editor.currentPhone || '未设置' }}。请输入新的完整手机号。</p>
        <el-form-item label="新手机号" prop="phone"><el-input v-model="editor.form.phone" inputmode="tel" autocomplete="tel" maxlength="11" placeholder="请输入11位手机号" /></el-form-item>
      </template>
      <template v-else>
        <p class="profile-edit-hint">新密码需要包含大小写字母、数字和特殊符号，长度为 8–20 位。修改后需重新登录。</p>
        <el-form-item label="原密码" prop="password"><el-input v-model="editor.form.password" type="password" autocomplete="current-password" show-password placeholder="请输入原密码" /></el-form-item>
        <el-form-item label="新密码" prop="newpassword1"><el-input v-model="editor.form.newpassword1" type="password" autocomplete="new-password" show-password placeholder="请输入新密码" /></el-form-item>
        <el-form-item label="确认新密码" prop="confirmation"><el-input v-model="editor.form.confirmation" type="password" autocomplete="new-password" show-password placeholder="请再次输入新密码" /></el-form-item>
      </template>
    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button type="primary" :loading="editor.saving" @click="save">保存修改</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { updateProfile } from './api'

const emit = defineEmits(['saved'])
const formRef = ref()
const editor = reactive({ open: false, mode: 'phone', currentPhone: '', username: '', avatar: '', saving: false, form: { phone: '', password: '', newpassword1: '', confirmation: '' } })
const rules = computed(() => editor.mode === 'phone' ? {
  phone: [{ required: true, message: '请输入新手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的11位手机号', trigger: 'blur' }]
} : {
  password: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newpassword1: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{8,20}$/, message: '密码需包含大小写字母、数字和特殊符号，长度8–20位', trigger: 'blur' },
    { validator: (_rule, value, callback) => callback(value === editor.form.password ? new Error('新密码不能与原密码相同') : undefined), trigger: 'blur' }
  ],
  confirmation: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: (_rule, value, callback) => callback(value !== editor.form.newpassword1 ? new Error('两次输入的密码不一致') : undefined), trigger: 'blur' }]
})

function clearSecrets() {
  Object.assign(editor.form, { phone: '', password: '', newpassword1: '', confirmation: '' })
  formRef.value?.clearValidate()
}
function open(mode, profile) {
  if (editor.saving) return
  clearSecrets()
  editor.mode = mode
  editor.currentPhone = profile.phone || ''
  editor.username = profile.username
  editor.avatar = profile.avatar
  editor.open = true
}
async function save() {
  if (editor.saving) return
  editor.saving = true
  try {
    if (!await formRef.value?.validate().catch(() => false)) return
    const payload = { username: editor.username, avatar: editor.avatar, ...(editor.mode === 'phone' ? { phone: editor.form.phone.trim() } : { password: editor.form.password, newpassword1: editor.form.newpassword1 }) }
    const result = await updateProfile(payload)
    if (result === false) { ElMessage.error('保存未完成，请稍后重试'); return }
    ElMessage.success(editor.mode === 'phone' ? '手机号已更新' : '密码已修改，请重新登录')
    editor.open = false
    clearSecrets()
    emit('saved', editor.mode)
  } catch (error) {
    if (!error?.response && !error?.config) ElMessage.error(error.message || '保存失败')
  } finally { editor.saving = false }
}
defineExpose({ open })
</script>

<style scoped>
.profile-edit-hint { margin: 0 0 22px; color: var(--muted); font-size: 13px; line-height: 1.8; }
.el-form-item { margin-bottom: 24px; }
:deep(.el-form-item__label) { margin-bottom: 8px; font-size: 13px; }
:deep(.el-input__wrapper) { min-height: 42px; }
:deep(input:-webkit-autofill) { -webkit-box-shadow: 0 0 0 1000px #fff inset; -webkit-text-fill-color: var(--ink); }
</style>
