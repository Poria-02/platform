import request from '@/utils/request'

export const pageApi = {
  role: id => request({
    url: `/upms/role/${id}`,
    method: 'get'
  }),
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
  }),
  menuTree: () => request({
    url: '/upms/menu/tree',
    method: 'get'
  }),
  roleMenuIds: roleId => request({
    url: `/upms/menu/tree/${roleId}`,
    method: 'get'
  }),
  updateRoleMenus: data => request({
    url: '/upms/role/menu',
    method: 'put',
    data
  })
}
