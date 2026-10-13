import { http } from '@/core/http'
export const list = (params, config = {}) => http.get('/upms/client/page', { ...config, params })
export const create = data => http.post('/upms/client', data)
export const update = data => http.put('/upms/client', data)
export const remove = id => http.delete(`/upms/client/${encodeURIComponent(id)}`)
export const clearCache = () => http.delete('/upms/client/cache')
