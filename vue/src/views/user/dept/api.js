import request from '@/utils/request'

export const pageApi = {
  deptTree: params => request({
    url: '/upms/dept/tree',
    method: 'get',
    params
  }),
  saveDept: data => request({
    url: '/upms/dept',
    method: 'post',
    data
  }),
  updateDept: data => request({
    url: '/upms/dept',
    method: 'put',
    data
  }),
  deleteDept: id => request({
    url: `/upms/dept/${id}`,
    method: 'delete'
  })
}
