import { http } from '@/core/http'
export const list = params => http.get('/base/api/version/page', { params })
export const create = data => http.post('/base/api/version', data)
export const update = data => http.put('/base/api/version', data)
export const remove = id => http.delete(`/base/api/version/${encodeURIComponent(id)}`)
