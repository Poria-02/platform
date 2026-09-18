import request from '@/utils/request'

export const pageApi = {
  sensitiveLogs: params => request({
    url: '/upms/sensitive/log/page',
    method: 'get',
    params
  })
}
