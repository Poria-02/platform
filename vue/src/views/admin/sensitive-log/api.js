import { http } from '@/core/http'
export const list = (params, config = {}) => http.get('/upms/sensitive/log/page', { ...config, params })
export const detail = id => http.get(`/upms/sensitive/log/${id}`)
