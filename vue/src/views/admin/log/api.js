import { http } from '@/core/http'
export const list = params => http.get('/upms/log/page', { params })
export const remove = id => http.delete(`/upms/log/${id}`)
export const dictionaryItems = () => http.get('/upms/dict/item/page', { params: { type: 'log_type', current: 1, size: 1000 }, silent: true }).then(data => data.records || [])
