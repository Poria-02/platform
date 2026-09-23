import { http } from '@/core/http'
export const list = params => http.get('/upms/param/page', { params })
export const create = data => http.post('/upms/param', data)
export const update = data => http.put('/upms/param', data)
export const remove = id => http.delete(`/upms/param/${id}`)
export const dictionaryItems = () => http.get('/upms/dict/item/page', { params: { type: 'param_type', current: 1, size: 1000 }, silent: true }).then(data => data.records || [])
