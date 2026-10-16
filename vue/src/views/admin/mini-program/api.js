import { http } from '@/core/http'

const base = '/upms/social/details'
export const list = (params, config = {}) => http.get(base + '/page', { silent: true, ...config, params: { ...params, types: 'WX,MINI' } })
export const create = data => http.post(base, data, { silent: true })
export const update = data => http.put(base, data, { silent: true })
export const remove = id => http.delete(base + '/' + encodeURIComponent(id), { silent: true })
