<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑角色' : '新增角色'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="editor.form.roleName" placeholder="请输入角色名称" @update:model-value="clearFieldError('roleName')" />
      </el-form-item>
      <el-form-item label="角色编码" prop="roleCode">
        <el-input v-model="editor.form.roleCode" placeholder="请输入角色编码" @update:model-value="clearFieldError('roleCode')" />
      </el-form-item>
      <el-form-item label="描述" prop="roleDesc">
        <el-input v-model="editor.form.roleDesc" type="textarea" :rows="3" placeholder="请输入描述" @update:model-value="clearFieldError('roleDesc')" />
      </el-form-item>
      <el-form-item label="数据权限" prop="dsType">
        <el-select v-model="editor.form.dsType" style="width:100%" placeholder="请选择数据权限" @change="editor.form.dsScope = ''"><el-option v-for="option in dataScopeOptions" :key="option.value" :label="option.label" :value="option.value" /></el-select><p class="scope-hint" :class="{ 'scope-error': !scopeOption(editor.form.dsType) }">{{ scopeOption(editor.form.dsType)?.description || '此类型尚未支持，请选择以上四种数据权限之一。' }}</p>
      </el-form-item>
      <el-form-item label="数据权限范围（自定义部门）" prop="dsScope" class="form-field--wide">
        <template v-if="editor.form.dsType === 1">
          <el-tree-select :model-value="scopeIds(editor.form.dsScope)" :data="departments" :props="{ label: 'name', children: 'children' }" node-key="id" multiple show-checkbox check-strictly filterable style="width:100%" placeholder="选择允许访问的部门" @visible-change="loadDepartments" @update:model-value="editor.form.dsScope = $event.join(',')" />
          <span v-if="departmentError" class="scope-error">{{ departmentError }}</span>
          <p class="scope-hint">只允许选中的部门。选择父部门不会自动包含下级，需要逐个勾选。</p>
        </template><span v-else class="scope-hint">{{ editor.form.dsType === 0 ? '全部数据无需选择部门。' : '无需选择部门，范围由登录用户所属部门确定。' }}</span>
      </el-form-item>
      <p class="scope-hint form-field--wide">用户拥有多个角色时，数据范围合并；任一角色为“全部”时，可访问全部部门数据。</p>
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
  dataScopeOptions: { type: Array, required: true },
  scopeOption: { type: Function, required: true },
  departments: { type: Array, required: true },
  departmentError: { type: String, required: true },
  scopeIds: { type: Function, required: true },
  loadDepartments: { type: Function, required: true },
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true },
  dsTypeChoices: { type: Array, required: true }
})
const emit = defineEmits(["saved"])

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ roleName: [{ required: true, message: "请填写角色名称", trigger: 'submit' }], roleCode: [{ required: true, message: "请填写角色编码", trigger: 'submit' }], dsType: [{ required: true, message: "请填写数据权限", trigger: 'submit' }] }))

function showEditor(form, editing) {
  form.dsType = normalizeSelectValue(form.dsType, {  }, props.dsTypeChoices)
  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate) return
  showEditor({ roleName: '', roleCode: '', roleDesc: '', dsType: 2, dsScope: '', ...defaults }, false)
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

<style scoped>
.scope-error { color: #a94637; font-size: 13px; }
.scope-hint { margin: 8px 0 0; color: #737373; font-size: 13px; line-height: 1.6; }
.scope-hint.scope-error { color: #a94637; }
.menu-permission-hint { margin: 0 0 16px; color: #737373; font-size: 13px; }
</style>
