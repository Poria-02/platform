import { http } from '@/core/http'
export const list = params => http.get('/upms/log/page', { params })
export const remove = id => http.delete(`/upms/log/${id}`)
