import request from '@/utils/request'

export const pageApi = {
  socialDetails: params => request({
    url: '/upms/social/details/page',
    method: 'get',
    params
  }),
  saveSocialDetails: data => request({
    url: '/upms/social/details',
    method: 'post',
    data
  }),
  updateSocialDetails: data => request({
    url: '/upms/social/details',
    method: 'put',
    data
  }),
  deleteSocialDetails: id => request({
    url: `/upms/social/details/${id}`,
    method: 'delete'
  })
}
