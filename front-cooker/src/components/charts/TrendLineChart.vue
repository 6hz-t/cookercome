<template>
  <div class="trend-chart">
    <svg :viewBox="`0 0 ${width} ${height}`" preserveAspectRatio="none">
      <polyline class="line-bg" :points="bgPoints" />
      <polyline class="line-value" :points="points" />
      <circle
        v-for="(point, index) in coordinates"
        :key="`${index}-${point.y}`"
        class="line-dot"
        :cx="point.x"
        :cy="point.y"
        r="3.8"
      />
    </svg>
    <div class="x-axis">
      <span v-for="(item, index) in data" :key="`${item.label}-${index}`">{{ item.label }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  valueKey: {
    type: String,
    default: 'orderCount'
  }
})

const width = 560
const height = 200
const padding = 26

const values = computed(() => props.data.map((item) => Number(item?.[props.valueKey] || 0)))
const maxValue = computed(() => {
  const max = Math.max(...values.value, 0)
  return max <= 0 ? 1 : max
})

const coordinates = computed(() => {
  const count = props.data.length || 1
  const stepX = count > 1 ? (width - padding * 2) / (count - 1) : 0

  return props.data.map((item, index) => {
    const value = Number(item?.[props.valueKey] || 0)
    const x = padding + stepX * index
    const ratio = value / maxValue.value
    const y = height - padding - ratio * (height - padding * 2)
    return {
      x,
      y: Number.isFinite(y) ? y : height - padding
    }
  })
})

const points = computed(() => coordinates.value.map((item) => `${item.x},${item.y}`).join(' '))
const bgPoints = computed(() => {
  const count = props.data.length || 1
  const stepX = count > 1 ? (width - padding * 2) / (count - 1) : 0
  return props.data
    .map((_, index) => `${padding + stepX * index},${height - padding}`)
    .join(' ')
})
</script>

<style scoped>
.trend-chart {
  width: 100%;
}

svg {
  width: 100%;
  height: 220px;
  overflow: visible;
}

.line-bg {
  fill: none;
  stroke: #d0d6e1;
  stroke-width: 2;
  stroke-dasharray: 5 5;
}

.line-value {
  fill: none;
  stroke: #3d72ff;
  stroke-width: 3;
  stroke-linejoin: round;
  stroke-linecap: round;
}

.line-dot {
  fill: #ffffff;
  stroke: #3d72ff;
  stroke-width: 2;
}

.x-axis {
  display: grid;
  grid-auto-flow: column;
  justify-content: space-between;
  gap: 8px;
  color: #6b7380;
  font-size: 12px;
}

@media (max-width: 768px) {
  svg {
    height: 180px;
  }
}
</style>
