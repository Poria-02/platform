import { ElMessage, ElMessageBox } from 'element-plus'

export function useCrudPage(options) {
  const loading = ref(false)
  const saving = ref(false)
  const rows = ref([])
  const total = ref(0)
  const query = reactive({})
  const page = reactive({ current: 1, size: 10 })
  const form = reactive({})
  const dialog = reactive({ open: false, title: '' })
  const canCreate = computed(() => Boolean(options.saver))
  const canEdit = computed(() => Boolean(options.updater))
  const canDelete = computed(() => Boolean(options.remover))

  function payloadOf(response) { return response?.data ?? response ?? {} }
  function listOf(payload) { return Array.isArray(payload) ? payload : (payload.records ?? payload.rows ?? payload.list ?? payload.content ?? []) }
  function display(value) { return value === null || value === undefined || value === '' ? '-' : (typeof value === 'object' ? JSON.stringify(value) : value) }
  function resetForm(row = {}) {
    Object.keys(form).forEach(key => delete form[key])
    options.formFields.forEach(field => { form[field.prop] = row[field.prop] ?? field.default ?? '' })
    if (row[options.idKey] !== undefined) form[options.idKey] = row[options.idKey]
  }
  async function load() {
    loading.value = true
    try {
      const params = { ...query }
      if (options.paged) Object.assign(params, { current: page.current, size: page.size })
      const payload = payloadOf(await options.fetcher(params))
      rows.value = listOf(payload)
      total.value = Number(payload?.total ?? payload?.totalCount ?? rows.value.length)
    } catch (_) {
      rows.value = []
      total.value = 0
    } finally { loading.value = false }
  }
  function search() { page.current = 1; return load() }
  function resetQuery() { Object.keys(query).forEach(key => { query[key] = '' }); return search() }
  function openCreate() { resetForm(); dialog.title = `新增${options.title}`; dialog.open = true }
  function openEdit(row) { resetForm(row); dialog.title = `编辑${options.title}`; dialog.open = true }
  async function submit() {
    const isUpdate = form[options.idKey] !== undefined && form[options.idKey] !== ''
    const handler = isUpdate ? options.updater : options.saver
    if (!handler) return
    saving.value = true
    try {
      await handler({ ...form })
      ElMessage.success('保存成功')
      dialog.open = false
      await load()
    } finally { saving.value = false }
  }
  async function remove(row) {
    const id = row[options.idKey]
    if (id === undefined || id === null) return ElMessage.warning('缺少记录标识，无法删除')
    await ElMessageBox.confirm(`确认删除该${options.title}记录吗？`, '系统提示', { type: 'warning' })
    await options.remover(id)
    ElMessage.success('删除成功')
    await load()
  }
  onMounted(load)
  return { loading, saving, rows, total, query, page, form, dialog, canCreate, canEdit, canDelete, display, load, search, resetQuery, openCreate, openEdit, submit, remove }
}
