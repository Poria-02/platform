import { http } from '@/core/http'

export async function list(params) {
  const page = await http.get('/upms/token/page', { params })
  const ids = [...new Set((page.records || []).map(row => String(row.username ?? '')).filter(value => /^\d+$/.test(value)))]
  const users = await Promise.all(ids.map(id => http.get(`/upms/user/${id}`, { silent: true }).catch(() => null)))
  const names = new Map(users.filter(Boolean).map(user => [String(user.userId), user.username]))
  return { ...page, records: page.records.map(row => ({ ...row, username: names.get(String(row.username)) || row.username })) }
}
export const remove = token => http.delete(`/upms/token/${encodeURIComponent(token)}`)
