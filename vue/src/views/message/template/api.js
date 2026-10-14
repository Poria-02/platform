import { http } from '@/core/http'

const base = '/message/api/template'
export const list = (params, config = {}) => http.get(base + '/page', { silent: true, ...config, params })
export const detail = (id, config = {}) => http.get(base + '/' + encodeURIComponent(id), { silent: true, ...config })
export const create = data => http.post(base, data, { silent: true })
export const update = data => http.put(base, data, { silent: true })
export const remove = id => http.delete(base + '/' + encodeURIComponent(id), { silent: true })
export const changeStatus = (id, status) => http.post(base + '/changeStatus/' + encodeURIComponent(id) + '/' + status, null, { silent: true })
