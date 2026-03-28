<template>
  <el-dialog v-model="visible" :title="title" width="900px" top="5vh" @open="initMap">
    <div class="map-toolbar">
      <el-input
        v-model="mapSearchKeyword"
        placeholder="请输入搜索关键词，例如：天府广场"
        clearable
        @keyup.enter="searchLocation"
      />
      <el-button type="primary" :loading="searching" @click="searchLocation">搜索</el-button>
      <el-button @click="locateInitial">定位初始位置</el-button>
    </div>
    <div class="coord-toolbar">
      <el-input v-model="manualLng" placeholder="请输入经度，例如：116.331398" clearable @keyup.enter="locateByCoordinate" />
      <el-input v-model="manualLat" placeholder="请输入纬度，例如：39.897445" clearable @keyup.enter="locateByCoordinate" />
      <el-button @click="locateByCoordinate">坐标定位</el-button>
    </div>

    <div v-if="mapError" class="map-error-block">
      <div class="map-error">{{ mapError }}</div>
      <pre v-if="mapDebugText" class="map-debug">{{ mapDebugText }}</pre>
      <div class="map-error-actions">
        <el-button size="small" type="primary" :loading="mapInitializing" @click="retryLoadMap">重试</el-button>
        <el-button size="small" text @click="copyDebugInfo">复制调试信息</el-button>
      </div>
    </div>
    <div v-loading="mapInitializing" ref="mapContainer" class="map-container" />

    <div class="pick-result" v-if="pickedPoint">
      <span>纬度：{{ pickedPoint.lat.toFixed(6) }}</span>
      <span>经度：{{ pickedPoint.lng.toFixed(6) }}</span>
      <span class="addr">地址：{{ pickedAddress || '地址解析中...' }}</span>
      <div class="addr-detail">详细地址：{{ pickedAddressDetail || '-' }}</div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="confirmSelection">使用此位置</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getBMapDebugInfo, loadBMapGL } from '@/utils/bmapLoader'
import { isValidCoordinate, reverseGeocodeByPoint } from '@/utils/location'

const DEFAULT_POINT = {
  lng: 114.14,
  lat: 30.67
}

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Baidu Map Picker'
  },
  initialLocation: {
    type: Object,
    default: () => ({
      lng: 0,
      lat: 0,
      address: ''
    })
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

const mapContainer = ref(null)
const mapSearchKeyword = ref('')
const mapError = ref('')
const mapInitializing = ref(false)
const mapDebugInfo = ref(null)
const searching = ref(false)
const manualLng = ref('')
const manualLat = ref('')

const pickedPoint = ref(null)
const pickedAddress = ref('')
const pickedAddressDetail = ref('')

let map = null
let marker = null
let geocoder = null
let localSearch = null
let clickListenerBound = false
let mapInitAttempt = 0

const mapDebugText = computed(() => {
  if (!mapDebugInfo.value) return ''
  const info = mapDebugInfo.value

  const scriptLines = Array.isArray(info.scripts) && info.scripts.length
    ? info.scripts.map((script) => {
      const state = script.readyState || '-'
      const source = script.managed ? 'managed' : 'external'
      return `#${script.index} [${source}] ${state} ${script.src}`
    }).join('\n')
    : 'No Baidu map scripts found'

  return [
    `code: ${info.code || '-'}`,
    `attempt: ${info.attempt || mapInitAttempt || '-'}`,
    `online: ${String(info.online)}`,
    `protocol: ${info.pageProtocol || '-'}`,
    `path: ${info.pagePath || '-'}`,
    `scriptCount: ${Number(info.scriptCount || 0)}`,
    `timestamp: ${info.timestamp || '-'}`,
    'scripts:',
    scriptLines
  ].join('\n')
})

function normalizeLocation(location = {}) {
  const lng = Number(location.lng || 0)
  const lat = Number(location.lat || 0)
  return {
    lng: Number.isFinite(lng) ? lng : 0,
    lat: Number.isFinite(lat) ? lat : 0,
    address: String(location.address || '').trim()
  }
}

function getInitialLocation() {
  const loc = normalizeLocation(props.initialLocation)
  const hasPoint = (loc.lng !== 0 || loc.lat !== 0)
  if (hasPoint) {
    return loc
  }
  return {
    ...DEFAULT_POINT,
    address: ''
  }
}

async function initMap(forceReload = false) {
  mapError.value = ''
  mapDebugInfo.value = null
  mapInitializing.value = true
  mapInitAttempt += 1
  
  // 等待对话框完全渲染
  await nextTick()
  await new Promise(resolve => setTimeout(resolve, 100))
  await nextTick()

  try {
    console.log('[百度地图选择器] 加载 BMapGL...')
    await loadBMapGL({
      retry: 2,
      timeoutMs: 15000,
      forceReload,
      debugLabel: `BaiduMapPickerDialog.initMap#${mapInitAttempt}`
    })

    if (!mapContainer.value) {
      console.error('[百度地图选择器] 地图容器未找到')
      mapError.value = '地图容器未找到'
      return
    }

    if (!map) {
      console.log('[百度地图选择器] 创建地图实例...')
      map = new window.BMapGL.Map(mapContainer.value)
      map.enableScrollWheelZoom(true)
      geocoder = new window.BMapGL.Geocoder()

      localSearch = new window.BMapGL.LocalSearch(map, {
        onSearchComplete: (result) => {
          searching.value = false
          if (!result || result.getCurrentNumPois() === 0) {
            ElMessage.warning('未找到相关结果')
            return
          }

          const poi = result.getPoi(0)
          if (!poi || !poi.point) {
            ElMessage.warning('无法获取该地点坐标')
            return
          }

          setPoint(poi.point, poi.address || poi.title || '')
          map.centerAndZoom(poi.point, 16)
        }
      })
    }

    if (!clickListenerBound) {
      map.addEventListener('click', (event) => {
        if (!event?.latlng) return
        setPoint(event.latlng)
      })
      clickListenerBound = true
    }

    const initial = getInitialLocation()
    const point = new window.BMapGL.Point(initial.lng, initial.lat)
    const zoom = initial.lng === DEFAULT_POINT.lng && initial.lat === DEFAULT_POINT.lat ? 12 : 16
    
    console.log('[百度地图选择器] 设置地图中心点:', point)
    map.centerAndZoom(point, zoom)
    setPoint(point, initial.address)
  } catch (error) {
    mapError.value = `地图加载失败 (${error?.code || 'unknown'})`
    mapDebugInfo.value = error?.debugInfo || getBMapDebugInfo()
    console.error('[百度地图选择器] 地图初始化失败', error, mapDebugInfo.value)
  } finally {
    mapInitializing.value = false
  }
}

async function setPoint(point, addressText = '') {
  if (!map || !point) return

  if (!marker) {
    marker = new window.BMapGL.Marker(point)
    map.addOverlay(marker)
  } else {
    marker.setPosition(point)
  }

  pickedPoint.value = {
    lng: Number(point.lng),
    lat: Number(point.lat)
  }
  manualLng.value = String(Number(point.lng).toFixed(8))
  manualLat.value = String(Number(point.lat).toFixed(8))

  if (addressText) {
    pickedAddress.value = addressText
  } else {
    pickedAddress.value = ''
  }

  pickedAddressDetail.value = ''
  if (!geocoder) return
  try {
    const reverse = await reverseGeocodeByPoint(point.lng, point.lat)
    pickedAddress.value = reverse?.address || pickedAddress.value || ''
    pickedAddressDetail.value = reverse?.addressDetail || ''
  } catch (error) {
    pickedAddressDetail.value = ''
  }
}

function searchLocation() {
  const keyword = mapSearchKeyword.value.trim()
  if (!keyword || !localSearch) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  searching.value = true
  localSearch.search(keyword)
}

function locateByCoordinate() {
  if (!map) return

  const lng = Number(manualLng.value)
  const lat = Number(manualLat.value)

  if (!Number.isFinite(lng) || !Number.isFinite(lat)) {
    ElMessage.warning('请输入有效的经纬度')
    return
  }

  if (!isValidCoordinate(lng, lat)) {
    ElMessage.warning('经纬度范围无效')
    return
  }

  const point = new window.BMapGL.Point(lng, lat)
  map.centerAndZoom(point, 16)
  setPoint(point)
}

function locateInitial() {
  if (!map) return
  const initial = getInitialLocation()
  const point = new window.BMapGL.Point(initial.lng, initial.lat)
  const zoom = initial.lng === DEFAULT_POINT.lng && initial.lat === DEFAULT_POINT.lat ? 12 : 16
  map.centerAndZoom(point, zoom)
  setPoint(point, initial.address)
}

function retryLoadMap() {
  initMap(true)
}

async function copyDebugInfo() {
  if (!mapDebugText.value) {
    ElMessage.warning('没有调试信息可复制')
    return
  }

  try {
    await navigator.clipboard.writeText(mapDebugText.value)
    ElMessage.success('调试信息已复制')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}

function confirmSelection() {
  if (!pickedPoint.value) {
    ElMessage.warning('请先在地图上选择一个位置')
    return
  }

  emit('confirm', {
    lng: Number(pickedPoint.value.lng.toFixed(8)),
    lat: Number(pickedPoint.value.lat.toFixed(8)),
    address: pickedAddress.value || ''
  })

  visible.value = false
}
</script>

<style scoped>
.map-toolbar {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 10px;
  margin-bottom: 10px;
}

.coord-toolbar {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 10px;
}

.map-container {
  width: 100%;
  height: 460px;
  border-radius: 10px;
  border: 1px solid #e5eaf2;
  overflow: hidden;
}

.map-error-block {
  margin-bottom: 10px;
}

.map-error {
  margin-bottom: 10px;
  color: #f56c6c;
}

.map-debug {
  margin: 0 0 8px;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid #f4d3d3;
  background: #fff7f7;
  color: #6b7280;
  white-space: pre-wrap;
  line-height: 1.4;
  font-size: 12px;
  max-height: 180px;
  overflow: auto;
}

.map-error-actions {
  display: flex;
  gap: 8px;
}

.pick-result {
  margin-top: 10px;
  display: grid;
  grid-template-columns: auto auto 1fr;
  gap: 10px;
  color: #4e5f76;
  font-size: 13px;
}

.pick-result .addr {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pick-result .addr-detail {
  grid-column: 1 / -1;
  color: #6b7280;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .map-toolbar,
  .coord-toolbar,
  .pick-result {
    grid-template-columns: 1fr;
  }

  .map-container {
    height: 360px;
  }
}
</style>
