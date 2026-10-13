import { http } from '@/core/http'
import { mutateDictionary } from '@/core/dictionaries'
import { exportDictionary, importDictionary } from '@/core/dictionaryTransfer'
export const list = (params, config = {}) => http.get('/upms/dict/page', { ...config, params })
export const create = data => mutateDictionary(http.post('/upms/dict', data))
export const update = data => mutateDictionary(http.put('/upms/dict', data))
export const remove = id => mutateDictionary(http.delete(`/upms/dict/${id}`))
export const listItems = params => http.get('/upms/dict/item/page', { params })
export const createItem = data => mutateDictionary(http.post('/upms/dict/item', data))
export const updateItem = data => mutateDictionary(http.put('/upms/dict/item', data))
export const removeItem = id => mutateDictionary(http.delete(`/upms/dict/item/${id}`))
export const exportDict = (dictTypes = []) => exportDictionary('/upms/dict/export', { dictTypes })
export const importDict = file => mutateDictionary(importDictionary('/upms/dict/import', file))
