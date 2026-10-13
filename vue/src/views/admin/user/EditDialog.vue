<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑用户' : '新增用户'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="editor.form.username" placeholder="请输入用户名" @update:model-value="clearFieldError('username')" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="editor.form.password" type="password" show-password placeholder="请输入密码" @update:model-value="clearFieldError('password')" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="editor.form.phone" placeholder="请输入手机号" @update:model-value="clearFieldError('phone')" />
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-tree-select v-model="editor.form.deptId" :data="departments" :props="{ label: 'name', children: 'children' }" node-key="id" check-strictly filterable style="width:100%" placeholder="请选择所属部门" @visible-change="loadDepartments" /><span v-if="departmentError" class="department-error">{{ departmentError }}</span>
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="editor.form.role" multiple style="width:100%" placeholder="请选择角色" @update:model-value="clearFieldError('role')"><el-option v-for="option in roleChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="状态" prop="lockFlag">
        <el-select v-model="editor.form.lockFlag" style="width:100%" placeholder="请选择状态" @update:model-value="clearFieldError('lockFlag')"><el-option v-for="option in lockFlagChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
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
  departments: { type: Array, required: true },
  departmentError: { type: String, required: true },
  loadDepartments: { type: Function, required: true },
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  lockFlagChoices: { type: Array, required: true },
  roleChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const prepareEdit = row => ({ ...row, password: '', role: row.roleList?.map(role => role.roleId) || [] })

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ username: [{ required: true, message: "请填写用户名", trigger: 'submit' }], password: !editor.editing ? [{ required: true, message: "请填写密码", trigger: 'submit' }] : [], phone: [{ required: true, message: "请填写手机号", trigger: 'submit' }], deptId: [{ required: true, message: "请填写所属部门", trigger: 'submit' }], role: [{ required: true, message: "请填写角色", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.role = normalizeSelectValue(form.role, { multiple: true }, props.roleChoices)
  form.lockFlag = normalizeSelectValue(form.lockFlag, { dictionary: 'status_type' }, props.lockFlagChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ username: '', password: '', phone: '', deptId: '', role: [], lockFlag: '0', ...defaults }, false)
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

<style scoped>.department-error { color: #a94637; font-size: 13px; }</style>
