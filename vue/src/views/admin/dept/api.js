import { http } from '@/core/http'
import { fetchDictionary } from '@/core/dictionaries'

export const tree = (config = {}) => http.get('/upms/dept/tree', config)
export const detail = id => http.get(`/upms/dept/detail/${id}`)
export const typeItems = () => fetchDictionary('dept_type')

function payload(data) {
  return {
    ...(data.deptId != null ? { deptId: data.deptId } : {}),
    name: data.name?.trim(),
    parentId: data.parentId ?? 0,
    sort: data.sort ?? 0,
    type: data.type === '' || data.type == null ? null : Number(data.type),
    detailId: data.detailId || null
  }
}

export const create = data => http.post('/upms/dept', payload(data))
export const update = data => http.put('/upms/dept', payload(data))
export const remove = id => http.delete(`/upms/dept/${id}`)
