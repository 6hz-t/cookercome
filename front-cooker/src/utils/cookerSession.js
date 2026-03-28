const MESSAGE_COUNT_KEY = 'cooker_unread_count'
const LEGACY_MESSAGE_COUNT_KEY = 'cooker_message_unread'
const STATUS_REASON_KEY = 'cooker_status_reason_log_v1'

export function parseChefId() {
  const raw = localStorage.getItem('userId')
  const parsed = Number(raw)
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null
}

export function getUnreadMessageCount() {
  const latest = localStorage.getItem(MESSAGE_COUNT_KEY)
  const legacy = localStorage.getItem(LEGACY_MESSAGE_COUNT_KEY)
  const raw = Number(latest ?? legacy ?? 0)
  return Number.isFinite(raw) && raw > 0 ? Math.floor(raw) : 0
}

export function setUnreadMessageCount(count) {
  const safe = Number.isFinite(Number(count)) ? Math.max(0, Math.floor(Number(count))) : 0
  localStorage.setItem(MESSAGE_COUNT_KEY, String(safe))
  localStorage.setItem(LEGACY_MESSAGE_COUNT_KEY, String(safe))
  window.dispatchEvent(new CustomEvent('cooker-message-updated', { detail: { unreadCount: safe } }))
}

export function increaseUnreadMessageCount(step = 1) {
  const next = getUnreadMessageCount() + Math.max(1, Math.floor(Number(step) || 1))
  setUnreadMessageCount(next)
}

export function appendStatusReason(record) {
  try {
    const raw = localStorage.getItem(STATUS_REASON_KEY)
    const list = raw ? JSON.parse(raw) : []
    const next = Array.isArray(list) ? list : []
    next.unshift({
      ...record,
      at: new Date().toISOString()
    })
    localStorage.setItem(STATUS_REASON_KEY, JSON.stringify(next.slice(0, 20)))
  } catch (error) {
    localStorage.setItem(STATUS_REASON_KEY, JSON.stringify([{ ...record, at: new Date().toISOString() }]))
  }
}

export function getStatusReasonLog() {
  try {
    const raw = localStorage.getItem(STATUS_REASON_KEY)
    const list = raw ? JSON.parse(raw) : []
    return Array.isArray(list) ? list : []
  } catch (error) {
    return []
  }
}
