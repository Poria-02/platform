import request from '@/utils/request'

export const pageApi = {
  routeDetail: id => request({
    url: `/upms/route/detail/${id}`,
    method: 'get'
  }),
  routes: () => request({
    url: '/upms/route/list',
    method: 'get'
  }),
  saveRoute: data => request({
    url: '/upms/route/addOrUpdate',
    method: 'post',
    data
  }),
  deleteRoute: id => request({
    url: `/upms/route/delete/${id}`,
    method: 'delete'
  }),
  refreshRoutes: () => request({
    url: '/upms/route/refresh',
    method: 'put'
  })
}
