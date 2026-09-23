import { http } from '@/core/http'
export const list = params => http.get('/base/api/version/page', { params })
export const create = data => http.post('/base/api/version', data)
export const update = data => http.put('/base/api/version', data)
export const remove = id => http.delete(`/base/api/version/${encodeURIComponent(id)}`)
export const dictionaryItems = () => Promise.all(['app_name', 'app_os', 'app_update_type'].map(type => http.get('/upms/dict/item/page', { params: { type, current: 1, size: 1000 }, silent: true }))).then(pages => pages.flatMap(page => page.records || []))
