/**
 * Matches Hutool's AES/CFB/NoPadding setup used by Poria Auth.
 *
 * The server uses its 16-byte encode key as both the AES key and IV. Web Crypto
 * does not expose AES-CFB directly, so each CFB keystream block is derived with
 * one AES-CBC encryption using a zero IV (the first ciphertext block is AES(K,
 * feedback)). No credentials are persisted by this helper.
 */
const textEncoder = new TextEncoder()

function bytesToBase64(bytes) {
  let binary = ''
  const blockSize = 0x8000
  for (let offset = 0; offset < bytes.length; offset += blockSize) {
    binary += String.fromCharCode(...bytes.subarray(offset, offset + blockSize))
  }
  return window.btoa(binary)
}

async function encryptBlock(key, feedback) {
  // AES-CBC pads a 16-byte input. Its first ciphertext block is AES(K, feedback)
  // because the IV is all zeros, which is exactly the block primitive CFB needs.
  const encrypted = await window.crypto.subtle.encrypt({
      name: 'AES-CBC',
      iv: new Uint8Array(16)
    },
    key,
    feedback
  )
  return new Uint8Array(encrypted).subarray(0, 16)
}

export async function encryptAesCfb(plainText, encodeKey) {
  const keyBytes = textEncoder.encode(encodeKey || '')
  if (keyBytes.length !== 16) {
    throw new Error('认证加密密钥必须为 16 个 UTF-8 字节')
  }
  if (!window.crypto?.subtle) {
    throw new Error('当前浏览器不支持登录所需的 Web Crypto API')
  }

  const cryptoKey = await window.crypto.subtle.importKey(
    'raw',
    keyBytes, {
      name: 'AES-CBC'
    },
    false,
    ['encrypt']
  )
  const source = textEncoder.encode(plainText ?? '')
  const cipherText = new Uint8Array(source.length)
  let feedback = keyBytes.slice()

  for (let offset = 0; offset < source.length; offset += 16) {
    const stream = await encryptBlock(cryptoKey, feedback)
    const length = Math.min(16, source.length - offset)
    const cipherBlock = new Uint8Array(length)
    for (let index = 0; index < length; index += 1) {
      cipherBlock[index] = source[offset + index] ^ stream[index]
      cipherText[offset + index] = cipherBlock[index]
    }
    // For a partial final block CFB terminates here, so it is never reused.
    if (length === 16) feedback = cipherBlock
  }

  return bytesToBase64(cipherText)
}
