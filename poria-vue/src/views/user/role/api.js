import request from '@/utils/request'

export const pageApi = {
  roles: params => request({
    url: '/upms/role/page',
    method: 'get',
    params
  }),
  saveRole: data => request({
    url: '/upms/role',
    method: 'post',
    data
  }),
  updateRole: data => request({
    url: '/upms/role',
    method: 'put',
    data
  }),
  deleteRole: id => request({
    url: `/upms/role/${id}`,
    method: 'delete'
  })
}
