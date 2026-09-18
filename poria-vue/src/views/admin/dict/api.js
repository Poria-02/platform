import request from '@/utils/request'

export const pageApi = {
  dictionaries: params => request({
    url: '/upms/dict/page',
    method: 'get',
    params
  }),
  saveDictionary: data => request({
    url: '/upms/dict',
    method: 'post',
    data
  }),
  updateDictionary: data => request({
    url: '/upms/dict',
    method: 'put',
    data
  }),
  deleteDictionary: id => request({
    url: `/upms/dict/${id}`,
    method: 'delete'
  })
}
