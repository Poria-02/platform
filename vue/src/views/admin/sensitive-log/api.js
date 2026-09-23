import request from '@/utils/request'

export const pageApi = {
  sensitiveLog: id => request({
    url: `/upms/sensitive/log/${id}`,
    method: 'get'
  }),
  sensitiveLogs: params => request({
    url: '/upms/sensitive/log/page',
    method: 'get',
    params
  })
}
