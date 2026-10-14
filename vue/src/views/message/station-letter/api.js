import { http } from '@/core/http'
const pending = '/message/api/pending/task'
const type = 99
export const list = (params, config = {}) => http.get(`${pending}/user/page`, { silent: true, ...config, params: { ...params, type } })
export const detail = id => http.get(`${pending}/${encodeURIComponent(id)}`, { silent: true })
export const templates = (keywords = '') => http.get('/message/api/template/page', { silent: true, params: { current: 1, size: 20, type: '2', status: '1', keywords } })
export const create = data => http.post('/message/api/station/letter/sendStationLetter', data, { silent: true })
export const read = id => http.put(`${pending}/${encodeURIComponent(id)}`, null, { silent: true })
export const readAll = () => http.post(`${pending}/allToRead`, null, { silent: true })
export const unread = () => http.get(`${pending}/unDone/count`, { silent: true, params: { type } })
