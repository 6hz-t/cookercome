<template>
  <el-dialog v-model="visible" :title="title" width="920px" top="5vh" @open="planRoute">
    <div class="route-toolbar">
      <div class="route-point">
        <span class="point-label">Start</span>
        <span>{{ startPointText }}</span>
      </div>
      <div class="route-point">
        <span class="point-label">End</span>
        <span>{{ endPointText }}</span>
      </div>
      <el-button size="small" :loading="routeLoading" @click="retryPlanRoute">Re-plan</el-button>
    </div>

    <div v-if="routeError" class="route-error-block">
      <div class="route-error">{{ routeError }}</div>
      <pre v-if="routeDebugText" class="route-debug">{{ routeDebugText }}</pre>
      <div class="route-error-actions">
        <el-button size="small" type="primary" :loading="routeLoading" @click="retryPlanRoute">Retry</el-button>
        <el-button size="small" text @click="copyDebugInfo">Copy Debug</el-button>
      </div>
    </div>

    <div v-loading="routeLoading" ref="mapContainer" class="route-map" />
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getBMapDebugInfo, loadBMapGL } from '@/utils/bmapLoader'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Driving Route Planner'
  },
  startLocation: {
    type: Object,
    default: () => ({ lng: 0, lat: 0, address: '' })
  },
  endLocation: {
    type: Object,
    default: () => ({ lng: 0, lat: 0, address: '' })
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

const mapContainer = ref(null)
const routeLoading = ref(false)
const routeError = ref('')
const routeDebugInfo = ref(null)

let map = null
let planner = null
let planAttempt = 0

function normalizeLocation(location = {}) {
  const lng = Number(location.lng || 0)
  const lat = Number(location.lat || 0)
  return {
    lng: Number.isFinite(lng) ? lng : 0,
    lat: Number.isFinite(lat) ? lat : 0,
    address: String(location.address || '').trim()
  }
}

function hasPoint(location) {
  return Boolean(location && Number(location.lng) !== 0 && Number(location.lat) !== 0)
}

function pointText(location) {
  if (!hasPoint(location)) {
    return 'Not configured'
  }
  return `${Number(location.lat).toFixed(6)}, ${Number(location.lng).toFixed(6)}`
}

function createRouteError(message, code, extra = {}) {
  const error = new Error(message)
  error.code = code
  error.debugInfo = {
    ...getBMapDebugInfo(),
    ...extra,
    code,
    timestamp: new Date().toISOString()
  }
  return error
}

const startNormalized = computed(() => normalizeLocation(props.startLocation))
const endNormalized = computed(() => normalizeLocation(props.endLocation))

const startPointText = computed(() => pointText(startNormalized.value))
const endPointText = computed(() => pointText(endNormalized.value))

const routeDebugText = computed(() => {
  if (!routeDebugInfo.value) return ''
  const info = routeDebugInfo.value
  return [
    `code: ${info.code || '-'}`,
    `attempt: ${info.attempt || planAttempt || '-'}`,
    `online: ${String(info.online)}`,
    `protocol: ${info.pageProtocol || '-'}`,
    `path: ${info.pagePath || '-'}`,
    `scriptCount: ${Number(info.scriptCount || 0)}`,
    `status: ${info.status ?? '-'}`,
    `timestamp: ${info.timestamp || '-'}`
  ].join('\n')
})

async function planRoute(forceReload = false) {
  routeError.value = ''
  routeDebugInfo.value = null
  routeLoading.value = true
  planAttempt += 1

  await nextTick()

  try {
    await loadBMapGL({
      retry: 2,
      timeoutMs: 15000,
      forceReload,
      debugLabel: `BaiduRoutePlannerDialog.planRoute#${planAttempt}`
    })

    if (!mapContainer.value) return

    if (!map) {
      map = new window.BMapGL.Map(mapContainer.value)
      map.enableScrollWheelZoom(true)
    } else {
      map.clearOverlays()
    }

    const start = startNormalized.value
    const end = endNormalized.value

    if (!hasPoint(start) || !hasPoint(end)) {
      throw createRouteError('Start or end coordinate is missing', 'coords_missing', {
        attempt: planAttempt,
        start,
        end
      })
    }

    const startPoint = new window.BMapGL.Point(start.lng, start.lat)
    const endPoint = new window.BMapGL.Point(end.lng, end.lat)
    map.centerAndZoom(startPoint, 12)

    const PlannerClass = window.BMapGL.DrivingRouteLine || window.BMapGL.DrivingRoute

    await new Promise((resolve, reject) => {
      planner = null
      planner = new PlannerClass(map, {
        renderOptions: {
          map,
          autoViewport: true,
          enableDragging: true
        },
        onSearchComplete: () => {
          const successCode = typeof window.BMAP_STATUS_SUCCESS === 'number' ? window.BMAP_STATUS_SUCCESS : 0
          const status = typeof planner?.getStatus === 'function' ? planner.getStatus() : successCode
          if (status === successCode || status === 0) {
            resolve(true)
            return
          }
          reject(createRouteError('Driving route planning failed', 'route_search_failed', {
            attempt: planAttempt,
            status
          }))
        }
      })
      planner.search(startPoint, endPoint)
    })
  } catch (error) {
    routeError.value = `Route planning failed (${error?.code || 'unknown'})`
    routeDebugInfo.value = error?.debugInfo || getBMapDebugInfo()
    console.error('[BaiduRoutePlannerDialog] route planning failed', error, routeDebugInfo.value)
  } finally {
    routeLoading.value = false
  }
}

function retryPlanRoute() {
  planRoute(true)
}

async function copyDebugInfo() {
  if (!routeDebugText.value) {
    ElMessage.warning('No debug info to copy')
    return
  }

  try {
    await navigator.clipboard.writeText(routeDebugText.value)
    ElMessage.success('Debug info copied')
  } catch (error) {
    ElMessage.error('Copy failed, please copy manually')
  }
}
</script>

<style scoped>
.route-toolbar {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 10px;
}

.route-point {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 8px;
  background: #f5f7fb;
  color: #4f5f78;
  font-size: 13px;
}

.point-label {
  color: #2f4568;
  font-weight: 600;
}

.route-map {
  width: 100%;
  height: 500px;
  border-radius: 10px;
  border: 1px solid #e5eaf2;
  overflow: hidden;
}

.route-error-block {
  margin-bottom: 10px;
}

.route-error {
  margin-bottom: 8px;
  color: #f56c6c;
}

.route-debug {
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

.route-error-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .route-toolbar {
    grid-template-columns: 1fr;
  }

  .route-map {
    height: 360px;
  }
}
</style>
