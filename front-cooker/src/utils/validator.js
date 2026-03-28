/**
 * 验证身份证号（中国大陆 18 位）
 * @param {string} idCard - 身份证号
 * @returns {boolean} - 是否有效
 */
export function isValidIdCard(idCard) {
  if (!idCard) return false

  // 去除空格
  const card = String(idCard).trim()

  // 只验证 18 位长度
  return card.length === 18
}

/**
 * 验证手机号（中国大陆）
 * 支持 11 位手机号，以 1 开头，第二位为 3-9
 * @param {string} phone - 手机号
 * @returns {boolean} - 是否有效
 */
export function isValidPhone(phone) {
  if (!phone) return false
  
  // 去除空格和横杠
  const p = String(phone).trim().replace(/[-\s]/g, '')
  
  // 验证 11 位手机号
  return /^1[3-9]\d{9}$/.test(p)
}

/**
 * 验证邮箱
 * @param {string} email - 邮箱地址
 * @returns {boolean} - 是否有效
 */
export function isValidEmail(email) {
  if (!email) return false
  
  const reg = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return reg.test(String(email).trim())
}
