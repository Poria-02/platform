import { http } from '@/core/http'
import { mutateDictionary } from '@/core/dictionaries'
import { exportDictionary, importDictionary } from '@/core/dictionaryTransfer'
export const list = (params, config = {}) => http.get('/upms/treedict/page', { ...config, params })
export const create = data => mutateDictionary(http.post('/upms/treedict/save', { code: data.code, name: data.name, isTree: data.isTree, remark: data.remark }))
export const update = data => mutateDictionary(http.post('/upms/treedict/update', { id: data.id, name: data.name, remark: data.remark }))
export const remove = id => mutateDictionary(http.delete(`/upms/treedict/del/${id}`))
export const listItems = (code, parentId = '0') => http.get(`/upms/treedict/item/find_tree_items/${encodeURIComponent(code)}/${encodeURIComponent(parentId)}`)
export const createItem = data => mutateDictionary(http.post('/upms/treedict/item/add', data))
export const updateItem = data => mutateDictionary(http.put('/upms/treedict/item/update', data))
export const removeItem = id => mutateDictionary(http.delete(`/upms/treedict/item/del/${id}`))
export const exportDict = (dictTreeCode = []) => exportDictionary('/upms/treedict/item/export', { dictTreeCode })
export const importDict = file => mutateDictionary(importDictionary('/upms/treedict/item/import', file))
