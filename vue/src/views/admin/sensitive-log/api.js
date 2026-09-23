import { http } from '@/core/http'
export const list = params => http.get('/upms/sensitive/log/page', { params })
export const detail = id => http.get(`/upms/sensitive/log/${id}`)
