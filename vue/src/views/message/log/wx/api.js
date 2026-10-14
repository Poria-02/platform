import { http } from '@/core/http'

export const list = (params, config = {}) => http.get('/message/api/log/wxTemplate', { silent: true, ...config, params })
