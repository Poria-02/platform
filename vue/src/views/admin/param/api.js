import { http } from '@/core/http'
export const list = params => http.get('/upms/param/page', { params })
export const create = data => http.post('/upms/param', data)
export const update = data => http.put('/upms/param', data)
export const remove = id => http.delete(`/upms/param/${id}`)
