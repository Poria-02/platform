import request from '@/utils/request'

export const pageApi = {
  users: params => request({
    url: '/upms/user/page',
    method: 'get',
    params
  }),
  saveUser: data => request({
    url: '/upms/user',
    method: 'post',
    data
  }),
  updateUser: data => request({
    url: '/upms/user',
    method: 'put',
    data
  }),
  deleteUser: id => request({
    url: `/upms/user/${id}`,
    method: 'delete'
  })
}
