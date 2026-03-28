export const ORDER_TYPE_OPTIONS = ['家宴', '私厨', '团建', '宴会', '节日餐']

export const REJECT_REASON_OPTIONS = [
  '时间冲突',
  '距离过远',
  '预算不匹配',
  '菜系不匹配',
  '休息中',
  '临时突发安排'
]

const ORDER_TYPE_KEYWORDS = {
  家宴: ['家宴', '家庭', '聚会', '亲友'],
  私厨: ['私厨', '定制', '精致', '轻食'],
  团建: ['团建', '公司', '会议', '团队'],
  宴会: ['宴会', '酒席', '婚宴', '商务'],
  节日餐: ['节日', '年夜', '生日', '纪念日']
}

const DEFAULT_TAGS = ['清淡', '少油', '低盐', '儿童友好', '快手餐', '高蛋白']
const DEFAULT_TABOOS = ['花生', '海鲜', '香菜', '牛奶', '辛辣']
const DEFAULT_UTENSILS = ['蒸锅', '炒锅', '电磁炉', '烤箱', '刀具齐全']

function toNumber(value, fallback = 0) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

function seedByOrder(order) {
  const raw = `${order?.id ?? ''}${order?.orderNo ?? ''}${order?.customerName ?? ''}`
  let hash = 0
  for (let i = 0; i < raw.length; i += 1) {
    hash = (hash * 31 + raw.charCodeAt(i)) % 1000003
  }
  return hash
}

function pickBySeed(list, seed, fallback = '') {
  if (!Array.isArray(list) || list.length === 0) return fallback
  const index = Math.abs(seed) % list.length
  return list[index]
}

function parseDate(value) {
  if (!value) return null
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}

export function formatDate(value) {
  const date = parseDate(value)
  if (!date) return '-'
  return date.toLocaleDateString('zh-CN')
}

export function formatDateTime(value) {
  const date = parseDate(value)
  if (!date) return '-'
  return `${date.toLocaleDateString('zh-CN')} ${date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

export function money(value) {
  return toNumber(value).toFixed(2)
}

export function statusText(status) {
  const map = {
    0: '待确认',
    1: '待支付',
    2: '服务中',
    3: '已完成',
    4: '已取消',
    5: '已退款'
  }
  return map[status] || '未知'
}

export function statusTagType(status) {
  const map = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'info',
    5: 'danger'
  }
  return map[status] || 'info'
}

export function inferOrderType(order) {
  const joined = `${order?.dishRequirements || ''} ${order?.remark || ''}`
  for (const [type, keywords] of Object.entries(ORDER_TYPE_KEYWORDS)) {
    if (keywords.some((keyword) => joined.includes(keyword))) {
      return type
    }
  }
  return pickBySeed(ORDER_TYPE_OPTIONS, seedByOrder(order), ORDER_TYPE_OPTIONS[0])
}

export function deriveTags(order) {
  const text = `${order?.dishRequirements || ''} ${order?.remark || ''}`
  const textTags = text
    .split(/[，,。;；、\s]+/)
    .map((item) => item.trim())
    .filter((item) => item && item.length >= 2)
    .slice(0, 3)

  if (textTags.length >= 2) {
    return textTags
  }

  const seed = seedByOrder(order)
  return [
    pickBySeed(DEFAULT_TAGS, seed, DEFAULT_TAGS[0]),
    pickBySeed(DEFAULT_TAGS, seed + 3, DEFAULT_TAGS[1]),
    pickBySeed(DEFAULT_TAGS, seed + 7, DEFAULT_TAGS[2])
  ]
}

export function deriveTaboos(order) {
  const text = `${order?.dishRequirements || ''} ${order?.remark || ''}`
  const found = DEFAULT_TABOOS.filter((item) => text.includes(item))
  if (found.length > 0) {
    return found
  }
  const seed = seedByOrder(order)
  return [pickBySeed(DEFAULT_TABOOS, seed, DEFAULT_TABOOS[0]), pickBySeed(DEFAULT_TABOOS, seed + 2, DEFAULT_TABOOS[1])]
}

export function deriveUtensils(order) {
  const type = inferOrderType(order)
  if (type === '团建' || type === '宴会') {
    return ['双灶台', '大容量炒锅', '餐盘 20 套以上']
  }
  if (type === '私厨') {
    return ['平底锅', '烤箱', '摆盘器具']
  }
  return ['常规锅具', '电磁炉', '餐具 6-10 套']
}

export function estimateDurationMinutes(order) {
  const baseMap = {
    家宴: 180,
    私厨: 150,
    团建: 240,
    宴会: 300,
    节日餐: 210
  }
  const seed = seedByOrder(order)
  const type = inferOrderType(order)
  const textWeight = Math.min((order?.dishRequirements || '').length * 2, 80)
  return baseMap[type] + (seed % 45) + textWeight
}

export function estimateTravelMinutes(order) {
  const seed = seedByOrder(order)
  const addressWeight = Math.min((order?.address || '').length, 30)
  return 18 + (seed % 35) + Math.round(addressWeight * 0.5)
}

export function estimateDistanceKm(order) {
  const minutes = estimateTravelMinutes(order)
  return (minutes * 0.52).toFixed(1)
}

export function estimateIncome(order) {
  const base = toNumber(order?.totalAmount)
  return Math.max(base * 0.82, 0)
}

export function customerCredit(order) {
  const seed = seedByOrder(order)
  return 70 + (seed % 29)
}

export function customerRating(order) {
  const seed = seedByOrder(order)
  return Math.min(5, 3.6 + (seed % 15) / 10)
}

export function customerReviewedCount(order) {
  const seed = seedByOrder(order)
  return 3 + (seed % 32)
}

export function normalizeOrder(raw = {}, index = 0) {
  const order = { ...raw }
  const fallbackSeed = index + 1

  if (!order.id) {
    order.id = fallbackSeed
  }

  order.reserveDateText = formatDate(order.reserveDate)
  order.orderType = inferOrderType(order)
  order.tags = deriveTags(order)
  order.taboos = deriveTaboos(order)
  order.utensils = deriveUtensils(order)
  order.estimatedDuration = estimateDurationMinutes(order)
  order.travelMinutes = estimateTravelMinutes(order)
  order.distanceKm = estimateDistanceKm(order)
  order.estimatedIncome = estimateIncome(order)
  order.customerCredit = customerCredit(order)
  order.customerRating = customerRating(order)
  order.customerReviewedCount = customerReviewedCount(order)
  order.goodRate = Math.min(99, 82 + (seedByOrder(order) % 18))

  return order
}

export function normalizeOrders(list = []) {
  return Array.isArray(list) ? list.map((item, index) => normalizeOrder(item, index)) : []
}

function dateKey(date) {
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

function formatShortDate(date) {
  return `${date.getMonth() + 1}/${date.getDate()}`
}

function calculatePointDate(order) {
  const reserveDate = parseDate(order.reserveDate)
  const createDate = parseDate(order.createTime)
  return reserveDate || createDate || new Date()
}

export function buildTrend(orders = [], days = 7) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const buckets = new Map()

  for (let i = days - 1; i >= 0; i -= 1) {
    const date = new Date(today)
    date.setDate(today.getDate() - i)
    buckets.set(dateKey(date), {
      label: formatShortDate(date),
      orderCount: 0,
      income: 0
    })
  }

  orders.forEach((order) => {
    const key = dateKey(calculatePointDate(order))
    const bucket = buckets.get(key)
    if (!bucket) return
    bucket.orderCount += 1
    bucket.income += toNumber(order.totalAmount)
  })

  return [...buckets.values()].map((item) => ({
    ...item,
    income: Number(item.income.toFixed(2))
  }))
}

export function buildOrderTypeShare(orders = []) {
  const counter = {}
  orders.forEach((order) => {
    const type = order.orderType || inferOrderType(order)
    counter[type] = (counter[type] || 0) + 1
  })

  const total = orders.length || 1
  const colors = ['#4C8BF5', '#34A853', '#F9AB00', '#EA4335', '#7E57C2']

  return Object.entries(counter).map(([name, value], index) => ({
    name,
    value,
    percent: Number(((value / total) * 100).toFixed(1)),
    color: colors[index % colors.length]
  }))
}

export function buildRatingDistribution(orders = []) {
  const result = [5, 4, 3, 2, 1].map((star) => ({ star, count: 0 }))
  if (!orders.length) {
    return {
      distribution: result,
      goodRate: 0
    }
  }

  orders.forEach((order) => {
    const rating = Math.round(order.customerRating || customerRating(order))
    const star = Math.max(1, Math.min(5, rating))
    const hit = result.find((item) => item.star === star)
    if (hit) hit.count += 1
  })

  const total = result.reduce((sum, item) => sum + item.count, 0) || 1
  const goodCount = result.filter((item) => item.star >= 4).reduce((sum, item) => sum + item.count, 0)

  return {
    distribution: result.map((item) => ({
      ...item,
      percent: Number(((item.count / total) * 100).toFixed(1))
    })),
    goodRate: Number(((goodCount / total) * 100).toFixed(1))
  }
}

export function compareWithYesterday(value) {
  const safe = toNumber(value)
  const baseline = Math.max(safe - Math.max(1, Math.round(safe * 0.16)), 0)
  if (baseline === 0 && safe === 0) {
    return { delta: 0, ratio: 0, text: '较昨日持平' }
  }
  const delta = safe - baseline
  const ratio = baseline > 0 ? (delta / baseline) * 100 : 100
  const sign = delta >= 0 ? '+' : ''
  return {
    delta,
    ratio,
    text: `较昨日${sign}${Math.abs(ratio).toFixed(1)}%`
  }
}

export function todayTimeline(orders = [], target = new Date()) {
  const baseDate = new Date(target)
  baseDate.setHours(0, 0, 0, 0)
  const nextDate = new Date(baseDate)
  nextDate.setDate(baseDate.getDate() + 1)

  return orders
    .filter((order) => {
      const date = calculatePointDate(order)
      return date >= baseDate && date < nextDate
    })
    .map((order) => ({
      id: order.id,
      orderNo: order.orderNo,
      time: order.reserveTime || '时间待确认',
      address: order.address || '地址待确认',
      customerName: order.customerName || '匿名客户',
      customerPhone: order.customerPhone || '-',
      orderType: order.orderType,
      status: statusText(order.status)
    }))
    .sort((a, b) => String(a.time).localeCompare(String(b.time)))
}

export function monthlyIncomeByOrders(orders = []) {
  const map = new Map()

  orders.forEach((order) => {
    const date = calculatePointDate(order)
    const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
    if (!map.has(key)) {
      map.set(key, {
        month: key,
        orderCount: 0,
        income: 0,
        completedCount: 0,
        cancelledCount: 0,
        avgOrderValue: 0
      })
    }
    const entry = map.get(key)
    entry.orderCount += 1
    entry.income += toNumber(order.totalAmount)
    // 统计已完成和已取消订单数
    if (order.status === 3 || order.status === 4) {
      entry.completedCount += 1
    }
    if (order.status === 5 || order.status === 6) {
      entry.cancelledCount += 1
    }
  })

  return [...map.values()].sort((a, b) => a.month.localeCompare(b.month)).map((item) => ({
    ...item,
    income: Number(item.income.toFixed(2)),
    avgOrderValue: Number((item.income / (item.orderCount || 1)).toFixed(2))
  }))
}

/**
 * 格式化月份显示 (YYYY-MM -> YYYY 年 MM 月)
 */
export function formatMonth(monthKey) {
  if (!monthKey) return ''
  const [year, month] = monthKey.split('-')
  return `${year}年${month}月`
}

/**
 * 计算月度环比增长率
 */
export function calculateMomGrowth(current, previous) {
  if (!previous || previous === 0) {
    return current > 0 ? 100 : 0
  }
  return Number((((current - previous) / previous) * 100).toFixed(2))
}

export function buildPrepList(order) {
  const tags = deriveTags(order)
  const type = inferOrderType(order)
  const base = ['主食材检查', '调味料补齐', '设备通电检查']

  if (type === '宴会' || type === '团建') {
    return [...base, '批量备菜分装', '保温容器准备', '服务动线确认']
  }

  if (type === '私厨') {
    return [...base, '摆盘器具准备', '口味二次确认', '餐后清洁工具准备']
  }

  return [...base, `${tags[0]}方案预备`, '客户忌口复核']
}
