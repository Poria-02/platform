import { http } from '@/core/http'
export async function list({ current = 1, size = 10, roleName = '', roleCode = '' } = {}) {
  const roles = await http.get('/upms/role/list') || []
  const filtered = roles.filter(role =>
    (!roleName || role.roleName?.includes(roleName)) && (!roleCode || role.roleCode?.includes(roleCode)))
  const start = (current - 1) * size
  return { records: filtered.slice(start, start + size), total: filtered.length }
}
function rolePayload(data) {
  const dsType = Number(data.dsType)
  const dsScope = dsType === 1 ? [...new Set(String(data.dsScope || '').split(',').map(id => id.trim()).filter(Boolean))].join(',') : ''
  return { ...data, dsType, dsScope }
}
export const create = data => http.post('/upms/role', rolePayload(data))
export const update = data => http.put('/upms/role', rolePayload(data))
export const remove = id => http.delete(`/upms/role/${id}`)
export const menuTree = () => http.get('/upms/menu/tree')
export const assignedMenuIds = roleId => http.get(`/upms/menu/tree/${roleId}`)
export const assignMenus = data => http.put('/upms/role/menu', { ...data, menuIds: Array.isArray(data.menuIds) ? data.menuIds.join(',') : data.menuIds })
export const departmentTree = () => http.get('/upms/dept/tree')
