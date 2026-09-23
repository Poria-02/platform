import request from '@/utils/request'
import { upmsUrl } from '@/utils/api-url'

// 获取路由
export const getRouters = () => {
  return request({
    url: upmsUrl('/menu'),
    method: 'get',
    params: import.meta.env.VITE_MENU_PLATFORM ? { platform: import.meta.env.VITE_MENU_PLATFORM } : undefined
  })
}
