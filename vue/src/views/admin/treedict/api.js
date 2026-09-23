import request from '@/utils/request'

export const pageApi = {
  treeDictionaries: params => request({
    url: '/upms/treedict/page',
    method: 'get',
    params
  }),
  saveTreeDictionary: data => request({
    url: '/upms/treedict/save',
    method: 'post',
    data
  }),
  updateTreeDictionary: data => request({
    url: '/upms/treedict/update',
    method: 'post',
    data
  }),
  deleteTreeDictionary: id => request({
    url: `/upms/treedict/del/${id}`,
    method: 'delete'
  }),
  treeDictionaryItems: code => request({
    url: `/upms/treedict/item/detail/${encodeURIComponent(code)}`,
    method: 'get'
  }),
  saveTreeDictionaryItem: data => request({
    url: '/upms/treedict/item/add',
    method: 'post',
    data
  }),
  updateTreeDictionaryItem: data => request({
    url: '/upms/treedict/item/update',
    method: 'put',
    data
  }),
  deleteTreeDictionaryItem: id => request({
    url: `/upms/treedict/item/del/${id}`,
    method: 'delete'
  })
}
