import { http } from '@/core/http'
export const list = params => http.get('/upms/treedict/page', { params })
export const create = data => http.post('/upms/treedict/save', { code: data.code, name: data.name, isTree: data.isTree, remark: data.remark })
export const update = data => http.post('/upms/treedict/update', { id: data.id, name: data.name, remark: data.remark })
export const remove = id => http.delete(`/upms/treedict/del/${id}`)
export const listItems = (code, parentId = '0') => http.get(`/upms/treedict/item/find_tree_items/${encodeURIComponent(code)}/${encodeURIComponent(parentId)}`)
export const createItem = data => http.post('/upms/treedict/item/add', data)
export const updateItem = data => http.put('/upms/treedict/item/update', data)
export const removeItem = id => http.delete(`/upms/treedict/item/del/${id}`)
