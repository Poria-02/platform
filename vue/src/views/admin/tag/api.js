import { http } from '@/core/http'
export const list = params => http.get('/upms/tag/page', { params })
export const create = data => http.post('/upms/tag', data)
export const update = data => http.put('/upms/tag', data)
export const remove = id => http.delete(`/upms/tag/${id}`)
export const listItems = params => http.get('/upms/tagItem/page', { params })
export const createItem = data => http.post('/upms/tagItem', data)
export const updateItem = data => http.put('/upms/tagItem', data)
export const removeItem = id => http.delete(`/upms/tagItem/${id}`)
