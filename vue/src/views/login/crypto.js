const encoder = new TextEncoder()

function toBase64(bytes) {
  let result = ''
  for (let index = 0; index < bytes.length; index += 0x8000) {
    result += String.fromCharCode(...bytes.subarray(index, index + 0x8000))
  }
  return btoa(result)
}

// 与后端 AES/CFB/NoPadding 保持一致。
export async function encryptForAuth(value, encodeKey) {
  const keyBytes = encoder.encode(encodeKey)
  if (keyBytes.length !== 16) throw new Error('认证加密密钥必须是 16 字节')
  if (!crypto?.subtle) throw new Error('当前浏览器不支持 Web Crypto')
  const key = await crypto.subtle.importKey('raw', keyBytes, 'AES-CBC', false, ['encrypt'])
  const plain = encoder.encode(value || '')
  const cipher = new Uint8Array(plain.length)
  let feedback = keyBytes.slice()
  for (let offset = 0; offset < plain.length; offset += 16) {
    const stream = new Uint8Array(await crypto.subtle.encrypt({ name: 'AES-CBC', iv: new Uint8Array(16) }, key, feedback))
    const count = Math.min(16, plain.length - offset)
    const block = new Uint8Array(count)
    for (let index = 0; index < count; index += 1) {
      block[index] = plain[offset + index] ^ stream[index]
      cipher[offset + index] = block[index]
    }
    if (count === 16) feedback = block
  }
  return toBase64(cipher)
}
