export function optionsForField(field, dictionaryItems = []) {
  if (!field.dictionary) return field.options || []
  const items = dictionaryItems.filter(item => item.type === field.dictionary)
  if (!items.length) return field.options || []
  return [...items]
    .sort((a, b) => Number(a.sort ?? 0) - Number(b.sort ?? 0) || String(a.id).localeCompare(String(b.id), 'zh-CN', { numeric: true }))
    .map(item => ({ label: item.label, value: String(item.value) }))
}

export function displayResourceValue(value, column = {}, options = [], row) {
  if (column.format) return column.format(value, row)
  if (value === null || value === undefined || value === '') return '—'
  if (column.dictionary || options.length) {
    const values = Array.isArray(value) ? value : column.separator
      ? String(value).split(column.separator).map(item => item.trim()).filter(Boolean)
      : [value]
    return values.map(item => options.find(option => String(option.value) === String(item))?.label ?? String(item)).join('、')
  }
  return typeof value === 'object' ? JSON.stringify(value) : String(value)
}

export function normalizeSelectValue(value, field, options = []) {
  function normalize(item) {
    if (item === null || item === undefined || item === '') return item
    // 下拉框严格匹配值类型；普通选项保留数字/布尔值，字典使用字符串编码。
    const option = options.find(candidate => String(candidate.value) === String(item))
    return option ? option.value : field.dictionary ? String(item) : item
  }
  if (!field.multiple) return normalize(value)
  const values = Array.isArray(value) ? value : field.joinWith
    ? String(value ?? '').split(field.joinWith).map(item => item.trim()).filter(Boolean)
    : value === null || value === undefined || value === '' ? [] : [value]
  return values.map(normalize)
}
