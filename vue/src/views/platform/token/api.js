import request from '@/utils/request'

export const pageApi = {
  tokens: params => request({
    url: '/upms/token/page',
    method: 'get',
    params
  }),
  deleteToken: token => request({
    url: `/upms/token/${encodeURIComponent(token)}`,
    method: 'delete'
  })
}
