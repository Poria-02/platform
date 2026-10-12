import { http } from './http'

async function responseError(error) {
  let body = error.response?.data
  if (body instanceof Blob) {
    try { body = JSON.parse(await body.text()) } catch { body = null }
  }
  return new Error(body?.msg || error.message || '字典文件请求失败')
}

export async function importDictionary(url, file) {
  const data = new FormData()
  data.append('file', file)
  try {
    return await http.post(url, data, { timeout: 120000, silent: true })
  } catch (error) { throw await responseError(error) }
}

export async function exportDictionary(url, data) {
  try {
    const file = await http.post(url, data, { responseType: 'blob', timeout: 120000, silent: true })
    if (!(file instanceof Blob)) throw new Error('服务未返回字典文件')
    if (file.type.includes('json')) {
      const body = JSON.parse(await file.text())
      if (!Array.isArray(body)) throw new Error(body?.msg || '字典导出失败')
    }
    return file
  } catch (error) { throw await responseError(error) }
}

export function saveDictionary(file, filename) {
  const url = URL.createObjectURL(file)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  try { link.click() } finally {
    link.remove()
    setTimeout(() => URL.revokeObjectURL(url), 1000)
  }
}
