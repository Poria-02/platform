import request from '@/utils/request'

export const pageApi = {
  menuTree: params => request({
    url: '/upms/menu/tree',
    method: 'get',
    params
  }),
  saveMenu: data => request({
    url: '/upms/menu',
    method: 'post',
    data
  }),
  updateMenu: data => request({
    url: '/upms/menu',
    method: 'put',
    data
  }),
  deleteMenu: id => request({
    url: `/upms/menu/${id}`,
    method: 'delete'
  })
}
