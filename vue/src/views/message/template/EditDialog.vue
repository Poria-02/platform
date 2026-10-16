<template>
  <el-dialog v-model="editor.open" class="business-dialog" :title="editor.editing ? '编辑模板' : '新增模板'" width="min(760px, calc(100vw - 32px))" append-to-body :show-close="!editor.saving" :close-on-click-modal="!editor.saving" :close-on-press-escape="!editor.saving">
    <el-form ref="formRef" :model="editor.form" :rules="formRules" :validate-on-rule-change="false" :disabled="editor.saving" label-position="top" class="editor-form">
      <el-form-item label="模板标题" prop="title"><el-input v-model="editor.form.title" placeholder="请输入模板标题" @update:model-value="clearFieldError('title')" /></el-form-item>
      <el-form-item label="模板 Key" prop="templateKey"><el-input v-model="editor.form.templateKey" placeholder="请输入唯一的模板 Key" @update:model-value="clearFieldError('templateKey')" /></el-form-item>
      <el-form-item label="模板类型" prop="type"><el-select v-model="editor.form.type" style="width:100%" @change="clearFieldError('content')"><el-option v-for="option in templateTypes" :key="option.value" :label="option.label" :value="option.value" /></el-select></el-form-item>
      <el-form-item label="状态" prop="status"><el-select v-model="editor.form.status" style="width:100%"><el-option v-for="option in templateStatuses" :key="option.value" :label="option.label" :value="option.value" /></el-select></el-form-item>
      <el-form-item label="模板分类" prop="category"><el-input v-model="editor.form.category" placeholder="请输入模板分类（可选）" /></el-form-item>
      <el-form-item v-if="editor.form.type === '1'" label="短信平台" prop="platform"><el-select v-model="editor.form.platform" filterable allow-create default-first-option style="width:100%" placeholder="请选择短信平台"><el-option v-for="option in smsPlatforms" :key="option.value" :label="option.label" :value="option.value" /></el-select></el-form-item>
      <el-form-item label="模板内容" prop="content" class="form-field--wide">
        <el-input v-model="editor.form.content" type="textarea" :rows="8" placeholder="请输入模板内容" @update:model-value="clearFieldError('content')" />
        <p class="template-hint">{{ contentHint }}</p>
      </el-form-item>
      <el-form-item :label="editor.form.type === '4' ? '小程序跳转页面' : '消息跳转 URL'" prop="url" class="form-field--wide"><el-input v-model="editor.form.url" :placeholder="editor.form.type === '4' ? '例如 pages/order/detail?id=${orderId}（可选）' : '请输入消息跳转 URL（可选）'" /></el-form-item>
      <el-form-item label="参数映射配置" prop="paramMapping" class="form-field--wide"><el-input v-model="editor.form.paramMapping" type="textarea" :rows="3" placeholder="请输入参数映射配置（可选）" /></el-form-item>
    </el-form>
    <template #footer><el-button :disabled="editor.saving" @click="editor.open = false">取消</el-button><el-button :disabled="!(editor.editing ? canEdit : canCreate)" type="primary" v-stable-loading="editor.saving" :loading="editor.saving" @click="save">保存</el-button></template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { smsPlatforms, templateStatuses, templateTypes, validateTemplateContent } from './options'
import * as api from './api'

const props = defineProps({ canCreate: { type: Boolean, required: true }, canEdit: { type: Boolean, required: true } })
const emit = defineEmits(['saved'])
const editor = reactive({ open: false, editing: false, saving: false, form: {} })
const formRef = ref()
const contentHint = computed(() => ({
  '1': '填写短信正文或短信平台的模板编码。',
  '2': '填写 JSON 对象，包含 title、content；type 为跳转类型，params 为跳转参数。',
  '3': '填写 JSON 对象，维护公众号 App ID 与模板 ID 的对应关系。',
  '4': '填写 JSON 数组；每项包含 appId、wxTemplateId、config，config 使用 key/valueKey 映射模板字段与业务参数。AppId 暂未接入时可留空，接入后需与小程序账号配置一致。'
})[editor.form.type])
const formRules = computed(() => ({
  title: [{ required: true, whitespace: true, message: '请填写模板标题', trigger: 'blur' }],
  templateKey: [{ required: true, whitespace: true, message: '请填写模板 Key', trigger: 'blur' }],
  type: [{ required: true, message: '请选择模板类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  platform: [{ validator: (_rule, value, callback) => callback(editor.form.type === '1' && !String(value || '').trim() ? new Error('请选择短信平台') : undefined), trigger: 'change' }],
  content: [{ validator: (_rule, value, callback) => { const error = validateTemplateContent(value, editor.form.type); callback(error ? new Error(error) : undefined) }, trigger: 'blur' }]
}))

function clearFieldError(field) { formRef.value?.clearValidate(field) }
function showEditor(row, editing) {
  editor.form = {
    id: row.id, title: row.title || '', templateKey: row.templateKey || '',
    type: String(row.type ?? '1'), status: String(row.status ?? '1'),
    content: row.content || '', platform: row.platform || '', category: row.category || '',
    url: row.url || '', paramMapping: row.paramMapping || ''
  }
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
    const data = {
      title: editor.form.title.trim(), templateKey: editor.form.templateKey.trim(),
      type: editor.form.type, status: Number(editor.form.status), content: editor.form.content,
      platform: editor.form.platform.trim(), category: editor.form.category.trim(),
      url: editor.form.url, paramMapping: editor.form.paramMapping
    }
    if (editor.editing) data.id = editor.form.id
    const result = await (editor.editing ? api.update(data) : api.create(data))
    if (result === false) { ElMessage.error('保存未完成'); return }
    ElMessage.success('保存成功')
    editor.open = false
    emit('saved')
  } catch (error) {
    // 保存失败时保留当前填写内容。
    ElMessage.error(error?.response?.data?.msg || error.message || '模板保存失败')
  } finally { editor.saving = false }
}

defineExpose({ openCreate, openEdit })
</script>

<style scoped>
.template-hint { width: 100%; margin: 8px 0 0; color: var(--muted); font-size: 12px; line-height: 1.7; }
</style>
