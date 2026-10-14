export const templateTypes = [
  { label: '短信', value: '1' },
  { label: '站内信', value: '2' },
  { label: '微信公众号', value: '3' },
  { label: '小程序', value: '4' }
]

export const templateStatuses = [
  { label: '启用', value: '1' },
  { label: '停用', value: '2' }
]

export const smsPlatforms = [
  { label: '阿里云平台', value: 'ali' },
  { label: '互联网医院', value: 'ih' },
  { label: '移动云 MAS', value: 'mas' },
  { label: '测试默认', value: 'default' },
  { label: '联通', value: 'lt' },
  { label: '天翼云信', value: 'esurfing' },
  { label: '网易云信', value: 'ntes' }
]

export function validateTemplateContent(content, type) {
  if (!String(content || '').trim()) return '请填写模板内容'
  if (String(type) === '1') return ''
  let parsed
  try { parsed = JSON.parse(content) }
  catch { return '该类型的模板内容须为有效 JSON' }
  if (!parsed || typeof parsed !== 'object') return '模板内容须为 JSON 对象或数组'
  if (String(type) !== '4' && Array.isArray(parsed)) return '该类型的模板内容须为 JSON 对象'
  if (String(type) === '2' && (!String(parsed.title || '').trim() || !String(parsed.content || '').trim())) return '站内信模板须包含 title 和 content'
  return ''
}
