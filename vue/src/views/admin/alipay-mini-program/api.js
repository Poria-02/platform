import { http } from '@/core/http'

const base = '/upms/social/details'
export const list = (params, config = {}) => http.get(base + '/page', { silent: true, ...config, params: { ...params, type: 'ALI_MA' } })
export const create = data => http.post(base, { ...data, type: 'ALI_MA' }, { silent: true })
export const update = data => http.put(base, { ...data, type: 'ALI_MA' }, { silent: true })
export const remove = id => http.delete(base + '/' + encodeURIComponent(id), { silent: true })
