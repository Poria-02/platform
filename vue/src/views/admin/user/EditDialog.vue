<template>
  <el-dialog class="business-dialog user-editor-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" :show-close="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑用户' : '新增用户'" width="min(720px, calc(100vw - 32px))" align-center append-to-body @closed="clearPasswords">
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :disabled="editor.saving" :validate-on-rule-change="false" label-position="top" class="editor-form user-editor-form" @submit.prevent="save">
      <section class="user-editor-section">
        <div class="section-heading"><h3>基本资料</h3><p>维护用户的登录账号和联系方式。</p></div>
        <div class="user-editor-grid">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="editor.form.username" autocomplete="off" placeholder="请输入用户名" @update:model-value="clearFieldError('username')" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editor.form.phone" inputmode="tel" autocomplete="off" maxlength="11" placeholder="请输入完整的11位手机号" @update:model-value="clearFieldError('phone')" />
            <span v-if="editor.editing" class="field-hint">未修改时保留原手机号。</span>
          </el-form-item>
        </div>
      </section>
      <section class="user-editor-section">
        <div class="section-heading"><h3>组织与权限</h3><p>设置所属部门、角色和账号状态。</p></div>
        <div class="user-editor-grid">
          <el-form-item label="所属部门" prop="deptId">
            <el-tree-select v-model="editor.form.deptId" :data="departments" :props="{ label: 'name', children: 'children' }" node-key="id" popper-class="department-tree-popper" :indent="22" highlight-current check-strictly filterable style="width:100%" placeholder="请选择所属部门" @visible-change="loadDepartments" /><span v-if="departmentError" class="department-error">{{ departmentError }}</span>
            <span v-if="!administrator" class="assignment-hint">仅可选择本级及下级部门</span>
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="editor.form.role" multiple style="width:100%" placeholder="请选择角色" @update:model-value="clearFieldError('role')"><el-option v-for="option in roleChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
          </el-form-item>
          <el-form-item label="状态" prop="lockFlag">
            <el-select v-model="editor.form.lockFlag" style="width:100%" placeholder="请选择状态" @update:model-value="clearFieldError('lockFlag')"><el-option v-for="option in lockFlagChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
          </el-form-item>
        </div>
      </section>
      <section class="user-editor-section password-section">
        <div class="password-heading">
          <div class="section-heading"><h3>{{ editor.editing ? '修改密码' : '登录密码' }}</h3><p>{{ editor.editing ? '默认保留原密码，开启后填写新密码。' : '设置用户首次登录使用的密码。' }}</p></div>
          <el-switch v-if="editor.editing" v-model="editor.changePassword" aria-label="修改用户密码" @change="togglePasswordChange" />
        </div>
        <Transition name="section-expand">
          <div v-if="passwordActive" class="motion-expand">
            <div class="motion-expand__inner">
              <p class="password-hint">密码需包含大小写字母、数字和特殊符号，长度为 8–20 位。</p>
              <div class="user-editor-grid">
                <el-form-item :label="editor.editing ? '新密码' : '密码'" prop="password">
                  <el-input v-model="editor.form.password" type="password" autocomplete="new-password" show-password maxlength="20" placeholder="请输入密码" @update:model-value="handlePasswordInput" />
                </el-form-item>
                <el-form-item :label="editor.editing ? '确认新密码' : '确认密码'" prop="confirmation">
                  <el-input v-model="editor.form.confirmation" type="password" autocomplete="new-password" show-password maxlength="20" placeholder="请再次输入密码" @update:model-value="clearFieldError('confirmation')" />
                </el-form-item>
              </div>
            </div>
          </div>
        </Transition>
      </section>
    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button :disabled="!(editor.editing ? canEdit : canCreate)" type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">{{ editor.editing ? (editor.changePassword ? '保存并修改密码' : '保存修改') : '创建用户' }}</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { normalizeSelectValue } from '@/core/resourceValues'

import * as api from './api'

const props = defineProps({
  departments: { type: Array, required: true },
  administrator: { type: Boolean, required: true },
  departmentError: { type: String, required: true },
  loadDepartments: { type: Function, required: true },
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  lockFlagChoices: { type: Array, required: true },
  roleChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

async function prepareEdit(row) {
  const user = await api.detail(row.userId)
  if (!user) throw new Error('用户不存在，请刷新列表')
  return {
    userId: user.userId,
    username: user.username,
    phone: user.phone ?? '',
    deptId: user.deptId,
    lockFlag: user.lockFlag,
    role: user.roleList?.map(role => role.roleId) || [],
    password: ''
  }
}

const editor = reactive({ open: false, editing: false, saving: false, changePassword: false, originalPhone: '', form: {} })

const formRef = ref()
const passwordActive = computed(() => !editor.editing || editor.changePassword)

const formRules = computed(() => ({
  username: [{ required: true, message: '请填写用户名', trigger: 'submit' }],
  password: passwordActive.value ? [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{8,20}$/, message: '密码需包含大小写字母、数字和特殊符号，长度8–20位', trigger: 'blur' }
  ] : [],
  confirmation: passwordActive.value ? [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (_rule, value, callback) => callback(value !== editor.form.password ? new Error('两次输入的密码不一致') : undefined), trigger: 'blur' }
  ] : [],
  phone: [{ required: true, message: '请填写手机号', trigger: 'blur' }, {
    validator: (_rule, value, callback) => callback((editor.editing && value === editor.originalPhone) || /^1[3-9]\d{9}$/.test(value) ? undefined : new Error('请输入完整的11位手机号')), trigger: 'blur'
  }],
  deptId: [{ type: 'number', required: true, min: 1, transform: value => value == null || value === '' ? undefined : Number(value), message: '请选择所属部门', trigger: 'change' }],
  role: [{ type: 'array', required: true, min: 1, message: '请选择角色', trigger: 'change' }]
}))

function showEditor(form, editing) {
  if (editor.saving) return
  form.role = normalizeSelectValue(form.role, { multiple: true }, props.roleChoices)
  form.lockFlag = normalizeSelectValue(form.lockFlag, { dictionary: 'status_type' }, props.lockFlagChoices)
  editor.editing = editing
  editor.changePassword = false
  editor.originalPhone = form.phone
  form.password = ''
  form.confirmation = ''
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function clearPasswords() {
  editor.form.password = ''
  editor.form.confirmation = ''
  clearFieldError(['password', 'confirmation'])
}

function togglePasswordChange(enabled) {
  if (!enabled) clearPasswords()
  else nextTick(() => clearFieldError(['password', 'confirmation']))
}

function handlePasswordInput() {
  clearFieldError(['password', 'confirmation'])
}

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ username: '', password: '', phone: '', deptId: '', role: [], lockFlag: '0', ...defaults }, false)
}

async function openEdit(row) {
  if (!props.canEdit || editor.saving) return
  try { showEditor(await prepareEdit(row), true) }
  catch (error) { if (!error?.response && !error?.config) ElMessage.error(error?.message || '编辑信息加载失败') }
}

async function save() {
  if (editor.saving) return
  if (!(editor.editing ? props.canEdit : props.canCreate)) return
  editor.saving = true
  try {
    if (!(await formRef.value.validate().catch(() => false))) return
    const form = { ...editor.form }
    delete form.confirmation
    if (!passwordActive.value) delete form.password
    if (editor.editing && form.phone === editor.originalPhone) delete form.phone

    const result = await (editor.editing ? api.update(form) : api.create(form))
    if (result === false) { ElMessage.error('保存未完成'); return }
    ElMessage.success(editor.editing && editor.changePassword ? '用户资料和密码已更新' : '保存成功')
    editor.open = false
    clearPasswords()
    emit('saved')

  } catch {
    // 请求层显示错误，保留填写内容。
  } finally { editor.saving = false }
}

defineExpose({ openCreate, openEdit })

</script>

<style scoped>
:global(.business-dialog.user-editor-dialog) { max-height: calc(100dvh - 32px); }
:global(.business-dialog.user-editor-dialog .el-dialog__header),
:global(.business-dialog.user-editor-dialog .el-dialog__footer) { flex-shrink: 0; }
:global(.business-dialog.user-editor-dialog .el-dialog__body) { flex: 0 1 auto; max-height: none; scrollbar-gutter: stable; }
.department-error { color: #a94637; font-size: 13px; }
.assignment-hint { display: block; margin-top: 6px; color: var(--muted); font-size: 12px; }
.user-editor-form { display: block; }
.user-editor-section + .user-editor-section { margin-top: 4px; padding-top: 22px; border-top: 1px solid #eee; }
.section-heading { display: block; margin-bottom: 18px; }
.section-heading h3 { margin: 0; color: var(--ink); font-size: 14px; font-weight: 650; }
.section-heading p, .field-hint, .password-hint { margin: 6px 0 0; color: var(--muted); font-size: 12px; line-height: 1.7; }
.field-hint { display: block; width: 100%; }
.user-editor-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); column-gap: 20px; }
.password-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; }
.password-heading .section-heading { margin-bottom: 0; }
.password-hint { margin: 14px 0; }
.password-section :deep(.el-form-item) { margin-bottom: 8px; }
@media (max-width: 640px) { .user-editor-grid { grid-template-columns: 1fr; } }
</style>
