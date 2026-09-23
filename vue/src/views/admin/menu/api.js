import { http } from '@/core/http'
export async function list({ name = '', platform = '', ...params } = {}) {
  const tree = await http.get('/upms/menu/tree', { params }) || []
  if (!name && !platform) return tree
  function filter(nodes) {
    return nodes.flatMap(node => {
      const children = filter(node.children || [])
      const matches = (!name || node.name?.includes(name)) && (!platform || node.platform?.includes(platform))
      return matches || children.length ? [{ ...node, children: matches ? node.children : children }] : []
    })
  }
  return filter(tree)
}
export const create = data => http.post('/upms/menu', data)
export const update = data => http.put('/upms/menu', data)
export const remove = id => http.delete(`/upms/menu/${id}`)
export async function fetchMyMenus() {
  return http.get('/upms/menu', { params: { platform: 'admin' } })
}
