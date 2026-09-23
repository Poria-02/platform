import { http } from '@/core/http'
import { encryptForAuth } from '@/views/login/crypto'
export const list = params => http.get('/upms/user/page', { params })
export const roles = () => http.get('/upms/role/list')
const payload = async data => ({
  userId: data.userId,
  username: data.username,
  phone: data.phone,
  deptId: data.deptId,
  lockFlag: data.lockFlag,
  role: data.role || [],
  ...(data.password ? { password: await encryptForAuth(data.password, import.meta.env.VITE_AUTH_ENCODE_KEY || 'shanxincdJinkang') } : {})
})
export const create = async data => http.post('/upms/user', await payload(data))
export const update = async data => http.put('/upms/user', await payload(data))
export const remove = id => http.delete(`/upms/user/${id}`)
