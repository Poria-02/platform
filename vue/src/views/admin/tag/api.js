import request from '@/utils/request'

export const pageApi = {
  tags: params => request({
    url: '/upms/tag/page',
    method: 'get',
    params
  }),
  saveTag: data => request({
    url: '/upms/tag',
    method: 'post',
    data
  }),
  updateTag: data => request({
    url: '/upms/tag',
    method: 'put',
    data
  }),
  deleteTag: id => request({
    url: `/upms/tag/${id}`,
    method: 'delete'
  }),
  tagItems: params => request({
    url: '/upms/tagItem/page',
    method: 'get',
    params
  }),
  saveTagItem: data => request({
    url: '/upms/tagItem',
    method: 'post',
    data
  }),
  updateTagItem: data => request({
    url: '/upms/tagItem',
    method: 'put',
    data
  }),
  deleteTagItem: id => request({
    url: `/upms/tagItem/${id}`,
    method: 'delete'
  })
}
