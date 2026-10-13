<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑菜单' : '新增菜单'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="菜单名称" prop="name">
        <el-input v-model="editor.form.name" placeholder="请输入菜单名称" @update:model-value="clearFieldError('name')" />
      </el-form-item>
      <el-form-item label="路由 path" prop="path">
        <el-input v-model="editor.form.path" placeholder="请输入路由 path" @update:model-value="clearFieldError('path')" />
      </el-form-item>
      <el-form-item label="权限标识" prop="permission">
        <el-input v-model="editor.form.permission" placeholder="请输入权限标识" @update:model-value="clearFieldError('permission')" />
      </el-form-item>
      <el-form-item label="上级菜单 ID" prop="parentId">
        <el-input-number v-model="editor.form.parentId" :min="0" controls-position="right" style="width:100%" @update:model-value="clearFieldError('parentId')" />
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <IconPicker v-model="editor.form.icon" />
      </el-form-item>
      <el-form-item label="菜单类型" prop="type">
        <el-select v-model="editor.form.type" style="width:100%" placeholder="请选择菜单类型" @update:model-value="clearFieldError('type')"><el-option v-for="option in typeChoices" :key="option.value" :label="option.label" :value="option.value" /></el-select>
      </el-form-item>
      <el-form-item label="所属平台" prop="platform">
        <el-input v-model="editor.form.platform" placeholder="请输入所属平台" @update:model-value="clearFieldError('platform')" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="editor.form.sort" :min="0" controls-position="right" style="width:100%" @update:model-value="clearFieldError('sort')" />
      </el-form-item>
      <div class="path-hint"><strong>目录映射</strong><p>例如 <code>/admin/menu/index</code> 对应：</p><code>src/views/admin/menu/index.vue</code><br><code>src/views/admin/menu/api.js</code><p>相对路径按父菜单路径展开。按钮类型 1 不生成页面路由。</p><p v-if="editor.form.path && editor.form.path.startsWith('/')">当前路径将查找对应目录下的 <code>index.vue</code> 和 <code>api.js</code></p></div>
    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { normalizeSelectValue } from '@/core/resourceValues'

import * as api from './api'
import IconPicker from '@/ui/IconPicker.vue'

const props = defineProps({
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  typeChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const prepareEdit = row => ({ menuId: row.id, name: row.name ?? '', path: row.path ?? '', permission: row.permission ?? '', parentId: row.parentId ?? -1, icon: row.icon ?? '', type: row.type ?? '0', platform: row.platform ?? '', sort: row.sort ?? 0 })

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ name: [{ required: true, message: "请填写菜单名称", trigger: 'submit' }], type: [{ required: true, message: "请填写菜单类型", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.type = normalizeSelectValue(form.type, {  }, props.typeChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ name: '', path: '', permission: '', parentId: -1, icon: '', type: '0', platform: '', sort: 0, ...defaults }, false)
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

<style scoped>.path-hint { grid-column: 1 / -1; padding: 14px; border-radius: 4px; background: #f6f6f6; color: #595959; font-size: 12px; line-height: 1.6; }.path-hint p { margin: 6px 0; }.path-hint code { color: #565656; font-weight: 700; }.menu-icon-cell { display: inline-flex; align-items: center; gap: 7px; color: #737373; }.menu-icon-cell .el-icon { color: #8d8d8d; font-size: 16px; }</style>
