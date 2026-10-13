import { http } from '@/core/http'
export const list = (params, config = {}) => http.get('/upms/log/page', { ...config, params })
export const remove = id => http.delete(`/upms/log/${id}`)
