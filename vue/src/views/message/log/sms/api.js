import { http } from '@/core/http'

export const list = (params, config = {}) => http.get('/message/api/log/sms', { silent: true, ...config, params })
