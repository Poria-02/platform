export const sendStatuses = [
  { label: '未发送', value: '0' },
  { label: '发送成功', value: '1' },
  { label: '发送失败', value: '2' },
  { label: '正在发送', value: '3' }
]

function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(value)) return false
  const parts = value.match(/\d+/g).map(Number)
  const date = new Date(value.replace(' ', 'T'))
  return date.getFullYear() === parts[0] && date.getMonth() + 1 === parts[1] && date.getDate() === parts[2]
    && date.getHours() === parts[3] && date.getMinutes() === parts[4] && date.getSeconds() === parts[5]
}

export function logQueryError(query) {
  if (Boolean(query.beginDate) !== Boolean(query.endDate)) return '请选择完整的发送时间范围'
  if (!query.beginDate) return ''
  if (!validDate(query.beginDate) || !validDate(query.endDate)) return '发送时间格式无效，请重新选择'
  return query.beginDate > query.endDate ? '开始时间不能晚于结束时间' : ''
}

export function formatLogText(value) {
  if (value === null || value === undefined || value === '') return '—'
  try { return JSON.stringify(typeof value === 'string' ? JSON.parse(value) : value, null, 2) }
  catch { return String(value) }
}
