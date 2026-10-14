import { http } from '@/core/http'
const base = '/message/api/announcement'
const pending = '/message/api/pending/task'
const type = 98
export const list = (params, config = {}) => http.get(`${pending}/user/page`, { silent: true, ...config, params: { ...params, type } })
export const detail = id => http.get(`${pending}/${encodeURIComponent(id)}`, { silent: true })
export const recordList = (params, config = {}) => http.get(`${base}/page`, { silent: true, ...config, params })
export const recordDetail = id => http.get(`${base}/${encodeURIComponent(id)}`, { silent: true })
export const create = data => http.post(base, data, { silent: true })
export const update = (id, data) => http.put(`${base}/${encodeURIComponent(id)}`, data, { silent: true })
export const remove = id => http.delete(`${base}/${encodeURIComponent(id)}`, { silent: true })
export const status = (id, status) => http.put(`${base}/${encodeURIComponent(id)}/status`, { status }, { silent: true })
export const send = (id, userIds) => http.post(`${base}/${encodeURIComponent(id)}/send`, { userIds }, { silent: true })
export const read = id => http.put(`${pending}/${encodeURIComponent(id)}`, null, { silent: true })
export const readAll = () => http.post(`${pending}/allToRead`, null, { silent: true })
export const unread = () => http.get(`${pending}/unDone/count`, { silent: true, params: { type } })
