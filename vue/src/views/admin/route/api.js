import { http } from '@/core/http'
export const list = () => http.get('/upms/route/list')
export const detail = id => http.get(`/upms/route/detail/${id}`)
export const create = data => http.post('/upms/route/addOrUpdate', data)
export const update = data => http.post('/upms/route/addOrUpdate', data)
export const remove = id => http.delete(`/upms/route/delete/${id}`)
export const refresh = () => http.put('/upms/route/refresh')
