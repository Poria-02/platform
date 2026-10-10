import { http } from '@/core/http'

export const tree = () => http.get('/upms/dept/tree')
export const detail = id => http.get(`/upms/dept/detail/${id}`)

function payload(data) {
  return {
    ...(data.deptId != null ? { deptId: data.deptId } : {}),
    name: data.name?.trim(),
    parentId: data.parentId ?? 0,
    sort: data.sort ?? 0,
    type: data.type,
    detailId: data.detailId || null
  }
}

export const create = data => http.post('/upms/dept', payload(data))
export const update = data => http.put('/upms/dept', payload(data))
export const remove = id => http.delete(`/upms/dept/${id}`)
