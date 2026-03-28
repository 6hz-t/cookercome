import CryptoJS from 'crypto-js'

// 加密密钥（实际项目中应该从环境变量读取，并且更复杂）
const SECRET_KEY = 'cookercome-2026-secret-key-for-aes-encryption'
// AES 加密需要 16 位、24 位或 32 位密钥（对应 AES-128、AES-192、AES-256）
// 这里使用 32 位密钥实现 AES-256

/**
 * AES 加密密码
 * @param password 明文密码
 * @returns 加密后的字符串（Base64 编码）
 */
export function encryptPassword(password: string): string {
  return CryptoJS.AES.encrypt(password, SECRET_KEY).toString()
}

/**
 * AES 解密密码
 * @param encryptedPassword 加密后的字符串
 * @returns 解密后的明文密码
 */
export function decryptPassword(encryptedPassword: string): string {
  const bytes = CryptoJS.AES.decrypt(encryptedPassword, SECRET_KEY)
  return bytes.toString(CryptoJS.enc.Utf8)
}
