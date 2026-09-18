import request from '@/utils/request'

export const pageApi = {
  users: params => request({
    url: '/upms/user/page',
    method: 'get',
    params
  }),
  roles: params => request({
    url: '/upms/role/page',
    method: 'get',
    params
  }),
  routes: () => request({
    url: '/upms/route/list',
    method: 'get'
  }),
  tokens: params => request({
    url: '/upms/token/page',
    method: 'get',
    params
  }),
  logs: params => request({
    url: '/upms/log/page',
    method: 'get',
    params
  })
}
