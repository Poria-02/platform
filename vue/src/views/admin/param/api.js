import request from '@/utils/request'

export const pageApi = {
  publicParams: params => request({
    url: '/upms/param/page',
    method: 'get',
    params
  }),
  savePublicParam: data => request({
    url: '/upms/param',
    method: 'post',
    data
  }),
  updatePublicParam: data => request({
    url: '/upms/param',
    method: 'put',
    data
  }),
  deletePublicParam: id => request({
    url: `/upms/param/${id}`,
    method: 'delete'
  })
}
