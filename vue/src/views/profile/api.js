import { http } from '@/core/http'
import { encryptForAuth } from '@/views/login/crypto'

export const fetchProfile = () => http.get('/upms/user/me', { silent: true })

export async function updateProfile({ username, avatar, phone, password, newpassword1 }) {
  const data = { username, avatar }
  if (phone) data.phone = phone.trim()
  if (password && newpassword1) {
    const key = import.meta.env.VITE_AUTH_ENCODE_KEY || 'Poriabanxiaqiu02'
    const encrypted = await Promise.all([encryptForAuth(password, key), encryptForAuth(newpassword1, key)])
    data.password = encrypted[0]
    data.newpassword1 = encrypted[1]
  }
  return http.put('/upms/user/edit', data)
}
