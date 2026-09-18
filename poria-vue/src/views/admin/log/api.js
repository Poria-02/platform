import request from '@/utils/request'

export const pageApi = {
  logs: params => request({
    url: '/upms/log/page',
    method: 'get',
    params
  }),
  deleteLog: id => request({
    url: `/upms/log/${id}`,
    method: 'delete'
  })
}
