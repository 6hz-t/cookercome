import { loadBMapGL } from './bmapLoader'

const ORDER_LOCATION_KEY = 'cooker_order_locations_v1'
const CHEF_LOCATION_KEY = 'cooker_chef_location_v1'

function readOrderLocationMap() {
  try {
    const raw = localStorage.getItem(ORDER_LOCATION_KEY)
    const parsed = raw ? JSON.parse(raw) : {}
    return parsed && typeof parsed === 'object' ? parsed : {}
  } catch (error) {
    return {}
  }
}

function writeOrderLocationMap(map) {
  localStorage.setItem(ORDER_LOCATION_KEY, JSON.stringify(map))
}

function toNumber(value, fallback = 0) {
  const num = Number(value)
  return Number.isFinite(num) ? num : fallback
}

function normalizeLocation(location = {}) {
  return {
    lng: toNumber(location.lng, 0),
    lat: toNumber(location.lat, 0),
    address: String(location.address || '').trim()
  }
}

export function isValidCoordinate(lng, lat) {
  const numLng = Number(lng)
  const numLat = Number(lat)
  if (!Number.isFinite(numLng) || !Number.isFinite(numLat)) {
    return false
  }
  return numLng >= -180 && numLng <= 180 && numLat >= -90 && numLat <= 90
}

export function formatAddressComponents(components = {}) {
  const parts = [
    components.province,
    components.city,
    components.district,
    components.street,
    components.streetNumber
  ].filter((item) => String(item || '').trim())
  return parts.join(', ')
}

function hasPoint(location) {
  return Boolean(location && Number(location.lng) !== 0 && Number(location.lat) !== 0)
}

export function getOrderLocation(orderId) {
  const map = readOrderLocationMap()
  const key = String(orderId || '')
  if (!key || !map[key]) return null
  const location = normalizeLocation(map[key])
  if (!hasPoint(location) && !location.address) return null
  return location
}

export function setOrderLocation(orderId, location) {
  const key = String(orderId || '')
  if (!key) return null
  const map = readOrderLocationMap()
  const normalized = normalizeLocation(location)
  map[key] = normalized
  writeOrderLocationMap(map)
  return normalized
}

export function getChefLocation() {
  try {
    const raw = localStorage.getItem(CHEF_LOCATION_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    const normalized = normalizeLocation(parsed)
    if (!hasPoint(normalized) && !normalized.address) return null
    return normalized
  } catch (error) {
    return null
  }
}

export function setChefLocation(location) {
  const normalized = normalizeLocation(location)
  if (!hasPoint(normalized) && !normalized.address) {
    localStorage.removeItem(CHEF_LOCATION_KEY)
    return null
  }
  localStorage.setItem(CHEF_LOCATION_KEY, JSON.stringify(normalized))
  return normalized
}

export function locationToText(location) {
  if (!location) return 'Not selected'
  const lng = Number(location.lng || 0).toFixed(6)
  const lat = Number(location.lat || 0).toFixed(6)
  return `${lat}, ${lng}`
}

function buildMarkerUrl(location, title = 'Service Location') {
  const lat = encodeURIComponent(String(location.lat))
  const lng = encodeURIComponent(String(location.lng))
  const text = encodeURIComponent(location.address || title)
  const markerTitle = encodeURIComponent(title)
  return `https://api.map.baidu.com/marker?location=${lat},${lng}&title=${markerTitle}&content=${text}&output=html&src=webapp.cookercome`
}

function buildAddressUrl(address) {
  const text = encodeURIComponent(address)
  return `https://api.map.baidu.com/geocoder?address=${text}&output=html&src=webapp.cookercome`
}

function buildDrivingUrl(start, end, startTitle = 'Start', endTitle = 'End') {
  const startName = encodeURIComponent(startTitle || 'Start')
  const endName = encodeURIComponent(endTitle || 'End')
  const origin = `latlng:${start.lat},${start.lng}|name:${startName}`
  const destination = `latlng:${end.lat},${end.lng}|name:${endName}`
  return `https://api.map.baidu.com/direction?origin=${origin}&destination=${destination}&mode=driving&coord_type=bd09ll&output=html&src=webapp.cookercome`
}

export function openBaiduNavigation({
  location = null,
  address = '',
  title = 'Service Location',
  startLocation = null,
  startTitle = 'My Location'
} = {}) {
  const normalizedEnd = normalizeLocation(location || {})
  const normalizedStart = normalizeLocation(startLocation || getChefLocation() || {})

  if (hasPoint(normalizedStart) && hasPoint(normalizedEnd)) {
    const routeUrl = buildDrivingUrl(normalizedStart, normalizedEnd, startTitle, title)
    window.open(routeUrl, '_blank')
    return { mode: 'route' }
  }

  if (hasPoint(normalizedEnd)) {
    const markerUrl = buildMarkerUrl(normalizedEnd, title)
    window.open(markerUrl, '_blank')
    return { mode: 'marker' }
  }

  const text = String(address || '').trim()
  if (!text) {
    throw new Error('Address is empty, cannot open navigation')
  }

  window.open(buildAddressUrl(text), '_blank')
  return { mode: 'address' }
}

export async function geocodeAddress(address) {
  const text = String(address || '').trim()
  if (!text) return null

  await loadBMapGL()
  return new Promise((resolve) => {
    const geocoder = new window.BMapGL.Geocoder()
    geocoder.getPoint(text, (point) => {
      if (!point) {
        resolve(null)
        return
      }
      resolve({
        lng: Number(point.lng),
        lat: Number(point.lat),
        address: text
      })
    })
  })
}

export async function reverseGeocodeByPoint(lng, lat) {
  if (!isValidCoordinate(lng, lat)) {
    return null
  }

  await loadBMapGL()
  return new Promise((resolve) => {
    const geocoder = new window.BMapGL.Geocoder()
    const point = new window.BMapGL.Point(Number(lng), Number(lat))
    geocoder.getLocation(point, (result) => {
      if (!result) {
        resolve({
          lng: Number(lng),
          lat: Number(lat),
          address: '',
          addressDetail: '',
          addressComponents: {}
        })
        return
      }

      const components = result.addressComponents || {}
      resolve({
        lng: Number(lng),
        lat: Number(lat),
        address: result.address || '',
        addressDetail: formatAddressComponents(components),
        addressComponents: components
      })
    })
  })
}
