<template>
  <el-dialog class="business-dialog" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving" :show-close="!editor.saving" v-model="editor.open" :title="editor.editing ? '编辑路由' : '新增路由'" width="min(680px, calc(100vw - 32px))" append-to-body>
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :disabled="editor.saving" :validate-on-rule-change="false" label-position="top" class="editor-form">
      <el-form-item label="路由 ID" prop="routeId">
        <el-input v-model="editor.form.routeId" placeholder="请输入路由 ID" @update:model-value="clearFieldError('routeId')" />
      </el-form-item>
      <el-form-item label="名称" prop="routeName">
        <el-input v-model="editor.form.routeName" placeholder="请输入名称" @update:model-value="clearFieldError('routeName')" />
      </el-form-item>
      <el-form-item label="目标 URI" prop="uri">
        <el-input v-model="editor.form.uri" placeholder="请输入目标 URI" @update:model-value="clearFieldError('uri')" />
      </el-form-item>
      <el-form-item label="匹配规则" prop="predicates">
        <el-input v-model="editor.form.predicates" type="textarea" :rows="3" placeholder="请输入匹配规则" @update:model-value="clearFieldError('predicates')" />
      </el-form-item>
      <el-form-item label="过滤器" prop="filters">
        <el-input v-model="editor.form.filters" type="textarea" :rows="3" placeholder="请输入过滤器" @update:model-value="clearFieldError('filters')" />
      </el-form-item>
      <el-form-item label="顺序" prop="order">
        <el-input-number v-model="editor.form.order" :min="0" controls-position="right" style="width:100%" @update:model-value="clearFieldError('order')" />
      </el-form-item>

    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button :disabled="!(editor.editing ? canEdit : canCreate)" type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import * as api from './api'

const props = defineProps({
  canCreate: { type: Boolean, required: true },
  canEdit: { type: Boolean, required: true }
})
const emit = defineEmits(['saved'])

const editor = reactive({ open: false, editing: false, saving: false, form: {} })

const formRef = ref()

const formRules = computed(() => ({ routeId: [{ required: true, message: "请填写路由 ID", trigger: 'submit' }], uri: [{ required: true, message: "请填写目标 URI", trigger: 'submit' }] }))

function showEditor(form, editing) {

  editor.editing = editing
  editor.form = form
  editor.open = true
  nextTick(() => formRef.value?.clearValidate())
}

function clearFieldError(field) { formRef.value?.clearValidate(field) }

function openCreate(defaults = {}) {
  if (!props.canCreate || editor.saving) return
  showEditor({ routeId: '', routeName: '', uri: '', predicates: '', filters: '', order: 0, ...defaults }, false)
}

async function openEdit(row) {
  if (!props.canEdit || editor.saving) return
  try { showEditor({ ...row }, true) }
  catch (error) { if (!error?.response && !error?.config) ElMessage.error(error?.message || '编辑信息加载失败') }
}

async function save() {
  if (editor.saving || !(editor.editing ? props.canEdit : props.canCreate)) return
  editor.saving = true
  try {
    if (!(await formRef.value.validate().catch(() => false))) return
    const form = { ...editor.form }

    const result = await (editor.editing ? api.update(form) : api.create(form))
    if (result === false) { ElMessage.error('保存未完成'); return }
    ElMessage.success('保存成功，请手动刷新网关路由后生效')
    editor.open = false
    emit('saved')

  } catch {
    // 请求层显示错误，保留填写内容。
  } finally { editor.saving = false }
}

defineExpose({ openCreate, openEdit })

</script>
