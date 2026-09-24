<template>
  <div class="resource-page">
    <header class="page-heading">
      <div class="page-heading__motif" aria-hidden="true"><span /><span /><span /><i /><i /></div>
      <div class="page-heading__copy"><div class="page-eyebrow">PORIA / {{ section }}</div><h1>{{ title }}</h1><p>{{ description }}</p></div>
      <div class="page-heading__ornament" aria-hidden="true" />
    </header>

    <div class="resource-toolbar">
      <div v-if="filters.length" class="filter-bar">
        <template v-for="field in filters" :key="field.prop">
          <el-select v-if="field.type === 'select'" v-model="query[field.prop]" :placeholder="field.placeholder || `选择${field.label}`" clearable><el-option v-for="option in optionsFor(field)" :key="option.value" :label="option.label" :value="option.value" /></el-select>
          <el-input v-else v-model="query[field.prop]" :placeholder="field.placeholder || `搜索${field.label}`" clearable @keyup.enter="search" />
        </template>
        <el-button type="primary" plain @click="search">查询</el-button>
        <el-button text @click="resetSearch">重置</el-button>
      </div>
      <div class="page-heading__actions"><slot name="actions" :reload="load" /><el-button @click="load">刷新</el-button><el-button v-if="canCreate" type="primary" @click="openCreate">新增{{ singular || title }}</el-button></div>
    </div>
    <section class="surface-card">
      <div v-if="loadError" class="resource-error" role="alert"><span>{{ loadError }}</span><el-button link type="primary" @click="load">重试</el-button></div>
      <el-table v-loading="loading" :data="visibleRows" :row-key="idKey" :tree-props="{ children: 'children' }" :default-expand-all="false" class="data-table" empty-text="暂无数据" style="width:100%">
        <el-table-column v-for="(column, index) in columns" :key="column.prop" :prop="column.prop" :label="column.label" :min-width="column.width || 130" :show-overflow-tooltip="column.overflow !== false">
          <template #default="scope">
            <div v-if="treeMode && index === 0" class="tree-label" :style="{ paddingLeft: `${scope.row.__depth * 18}px` }">
              <button v-if="scope.row.__hasChildren" class="tree-label__toggle" type="button" :aria-expanded="isExpanded(scope.row)" @click="toggleNode(scope.row)"><span :class="{ 'tree-label__arrow--open': isExpanded(scope.row) }">›</span><slot :name="`cell-${column.prop}`" :row="scope.row" :value="scope.row[column.prop]">{{ display(scope.row[column.prop], column, scope.row) }}</slot></button>
              <span v-else class="tree-label__leaf"><span class="tree-label__spacer" /><slot :name="`cell-${column.prop}`" :row="scope.row" :value="scope.row[column.prop]">{{ display(scope.row[column.prop], column, scope.row) }}</slot></span>
            </div>
            <slot v-else :name="`cell-${column.prop}`" :row="scope.row" :value="scope.row[column.prop]">{{ display(scope.row[column.prop], column, scope.row) }}</slot>
          </template>
        </el-table-column>
        <el-table-column v-if="canEdit || canRemove || $slots['row-actions']" label="操作" min-width="190" fixed="right">
          <template #default="scope">
            <slot name="row-actions" :row="scope.row" :reload="load" />
            <el-button v-if="canEdit" link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button v-if="canRemove" link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="pagination && total > 0" class="table-footer">
        <span>共 {{ total }} 条记录</span>
        <el-pagination v-model:current-page="page.current" v-model:page-size="page.size" :page-sizes="[10, 20, 50]" :total="total" layout="sizes, prev, pager, next" @size-change="load" @current-change="load" />
      </div>
    </section>

    <slot name="below" :reload="load" />
    <el-dialog v-model="editor.open" :title="editor.editing ? `编辑${singular || title}` : `新增${singular || title}`" width="min(640px, calc(100vw - 28px))" append-to-body>
      <el-form ref="formRef" :model="editor.form" :rules="fieldRules" label-position="top" class="editor-form">
        <el-form-item v-for="field in fields.filter(item => !editor.editing || !item.createOnly)" :key="field.prop" :label="field.label" :prop="field.prop">
          <slot v-if="$slots[`field-${field.prop}`]" :name="`field-${field.prop}`" :field="field" :form="editor.form" />
          <el-input-number v-else-if="field.type === 'number'" v-model="editor.form[field.prop]" :min="field.min ?? 0" controls-position="right" style="width:100%" />
          <el-select v-else-if="field.type === 'select'" v-model="editor.form[field.prop]" :multiple="field.multiple" style="width:100%" :placeholder="`请选择${field.label}`"><el-option v-for="option in optionsFor(field)" :key="option.value" :label="option.label" :value="option.value" /></el-select>
          <el-input v-else v-model="editor.form[field.prop]" :type="field.type === 'textarea' ? 'textarea' : field.type === 'password' ? 'password' : 'text'" :rows="field.type === 'textarea' ? 3 : undefined" :disabled="editor.editing && field.immutable" :show-password="field.type === 'password'" :placeholder="field.placeholder || `请输入${field.label}`" />
        </el-form-item>
        <slot name="form-hint" :form="editor.form" />
      </el-form>
      <template #footer><el-button @click="editor.open = false">取消</el-button><el-button type="primary" :loading="editor.saving" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  title: { type: String, required: true },
  section: { type: String, default: '管理' },
  description: { type: String, default: '' },
  singular: { type: String, default: '' },
  api: { type: Object, required: true },
  idKey: { type: String, default: 'id' },
  columns: { type: Array, required: true },
  filters: { type: Array, default: () => [] },
  fields: { type: Array, default: () => [] },
  prepareEdit: { type: Function, default: row => ({ ...row }) },
  pagination: { type: Boolean, default: true },
  treeMode: { type: Boolean, default: false }
})
const emit = defineEmits(['saved', 'removed'])

const canCreate = computed(() => Boolean(props.api.create && props.fields.length))
const canEdit = computed(() => Boolean(props.api.update && props.fields.length))
const canRemove = computed(() => Boolean(props.api.remove))
const fieldRules = computed(() => Object.fromEntries(props.fields.filter(field => (!editor.editing || !field.createOnly) && (field.required || (!editor.editing && field.requiredOnCreate))).map(field => [field.prop, [{ required: true, message: `请填写${field.label}`, trigger: 'blur' }]])))
const loading = ref(false)
const loadError = ref('')
const rows = ref([])
const dictionaryItems = ref([])
const dictionaryLabels = computed(() => {
  const labels = new Map()
  for (const item of dictionaryItems.value) {
    if (!labels.has(item.type)) labels.set(item.type, new Map())
    labels.get(item.type).set(String(item.value), item.label)
  }
  return labels
})
const expandedIds = ref(new Set())
const visibleRows = computed(() => {
  if (!props.treeMode) return rows.value
  const visible = []
  function append(items, depth) {
    for (const item of items) {
      const { children, ...row } = item
      const hasChildren = Array.isArray(children) && children.length > 0
      visible.push({ ...row, __depth: depth, __hasChildren: hasChildren })
      if (hasChildren && expandedIds.value.has(item[props.idKey])) append(children, depth + 1)
    }
  }
  append(rows.value, 0)
  return visible
})
const total = ref(0)
const query = reactive(Object.fromEntries(props.filters.map(field => [field.prop, ''])))
const page = reactive({ current: 1, size: 10 })
const editor = reactive({ open: false, editing: false, saving: false, form: {} })
const formRef = ref()

function display(value, column, row) {
  if (column?.format) return column.format(value, row)
  if (value === null || value === undefined || value === '') return '—'
  if (column?.dictionary) {
    const labels = dictionaryLabels.value.get(column.dictionary)
    const values = column.separator ? String(value).split(column.separator).map(item => item.trim()).filter(Boolean) : [String(value)]
    return values.map(item => labels?.get(item) || item).join('、')
  }
  return typeof value === 'object' ? JSON.stringify(value) : String(value)
}
function optionsFor(field) {
  if (!field.dictionary) return field.options || []
  const items = dictionaryItems.value.filter(item => item.type === field.dictionary)
  return items.sort((a, b) => Number(a.sort) - Number(b.sort) || String(a.id).localeCompare(String(b.id), 'zh-CN', { numeric: true })).map(item => ({ label: item.label, value: String(item.value) }))
}

function isExpanded(row) { return expandedIds.value.has(row[props.idKey]) }
function toggleNode(row) {
  const next = new Set(expandedIds.value)
  if (next.has(row[props.idKey])) next.delete(row[props.idKey])
  else next.add(row[props.idKey])
  expandedIds.value = next
}

async function load() {
  loading.value = true
  try {
    const params = { ...Object.fromEntries(Object.entries(query).filter(([, value]) => value !== '' && value !== null && value !== undefined)), ...(props.pagination ? { current: page.current, size: page.size } : {}) }
    const [listData, items] = await Promise.all([
      props.api.list(params),
      props.api.dictionaryItems ? props.api.dictionaryItems().catch(() => []) : Promise.resolve([])
    ])
    const data = listData || {}
    dictionaryItems.value = Array.isArray(items) ? items : items?.records || []
    rows.value = Array.isArray(data) ? data : data.records || data.rows || data.list || []
    total.value = Number(Array.isArray(data) ? data.length : data.total ?? data.totalCount ?? rows.value.length)
    loadError.value = ''
  } catch (error) {
    loadError.value = error?.response?.status === 503 ? '当前服务暂不可用，请稍后重试' : (error?.response?.data?.msg || error?.message || '数据加载失败')
  } finally {
    loading.value = false
  }
}

function search() { page.current = 1; return load() }
function resetSearch() { for (const field of props.filters) query[field.prop] = ''; return search() }
function openCreate() {
  editor.editing = false
  editor.form = Object.fromEntries(props.fields.map(field => [field.prop, field.default ?? (field.multiple ? [] : field.type === 'number' ? 0 : '')]))
  editor.open = true
}
function openEdit(row) {
  editor.editing = true
  const form = props.prepareEdit(row)
  for (const field of props.fields) {
    if (field.multiple && field.joinWith && !Array.isArray(form[field.prop])) {
      form[field.prop] = String(form[field.prop] ?? '').split(field.joinWith).map(value => value.trim()).filter(Boolean)
    }
  }
  editor.form = form
  editor.open = true
}
async function save() {
  if (!(await formRef.value.validate().catch(() => false))) return
  editor.saving = true
  try {
    const form = { ...editor.form }
    for (const field of props.fields) {
      if (field.multiple && field.joinWith && Array.isArray(form[field.prop])) form[field.prop] = form[field.prop].join(field.joinWith)
    }
    const result = await (editor.editing ? props.api.update(form) : props.api.create(form))
    if (result === false) { ElMessage.error('保存未完成'); return }
    ElMessage.success('保存成功')
    editor.open = false
    await load()
    emit('saved')
  } catch {
    // HTTP 层显示保存错误，保留编辑内容。
  } finally { editor.saving = false }
}
async function remove(row) {
  try { await ElMessageBox.confirm(`确认删除这条${props.singular || '记录'}吗？`, '确认删除', { type: 'warning' }) } catch { return }
  try {
    const result = await props.api.remove(row[props.idKey])
    if (result === false) { ElMessage.error('删除未完成'); return }
    ElMessage.success('删除成功')
    await load()
    emit('removed')
  } catch {
    // HTTP 层显示删除错误。
  }
}

onMounted(load)
defineExpose({ load })
</script>

<style scoped>
.tree-label { display: flex; align-items: center; min-height: 24px; }
.tree-label__toggle, .tree-label__leaf { display: inline-flex; align-items: center; gap: 8px; min-width: 0; }
.tree-label__toggle { border: 0; padding: 0; background: transparent; color: inherit; font: inherit; cursor: pointer; text-align: left; }
.tree-label__toggle:hover { color: #14826b; }
.tree-label__toggle > span:first-child, .tree-label__spacer { display: inline-block; width: 16px; flex: 0 0 16px; font-size: 20px; line-height: 1; color: #7e929d; transition: transform .18s ease; }
.tree-label__arrow--open { transform: rotate(90deg); }
.resource-error { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin: 0 0 14px; padding: 10px 14px; border-radius: 9px; background: #fff1ed; color: #a94637; font-size: 13px; }
</style>
