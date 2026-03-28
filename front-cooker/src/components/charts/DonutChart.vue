<template>
  <div class="donut-wrap">
    <div class="donut" :style="{ background: gradient }">
      <div class="hole">
        <strong>{{ total }}</strong>
        <span>总单量</span>
      </div>
    </div>
    <div class="legend">
      <div v-for="item in data" :key="item.name" class="legend-item">
        <i :style="{ backgroundColor: item.color }" />
        <span class="name">{{ item.name }}</span>
        <span class="value">{{ item.value }} 单</span>
        <span class="percent">{{ item.percent }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
})

const total = computed(() => props.data.reduce((sum, item) => sum + Number(item.value || 0), 0))

const gradient = computed(() => {
  if (!props.data.length) {
    return 'conic-gradient(#d9dbe3 0deg, #d9dbe3 360deg)'
  }

  let start = 0
  const segments = props.data.map((item) => {
    const step = (Number(item.percent || 0) / 100) * 360
    const end = start + step
    const segment = `${item.color} ${start}deg ${end}deg`
    start = end
    return segment
  })

  if (start < 360) {
    segments.push(`#e8ecf3 ${start}deg 360deg`)
  }

  return `conic-gradient(${segments.join(', ')})`
})
</script>

<style scoped>
.donut-wrap {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 16px;
  align-items: center;
}

.donut {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hole {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #3b3f48;
}

.hole strong {
  font-size: 26px;
  line-height: 1.1;
}

.hole span {
  margin-top: 6px;
  font-size: 12px;
  color: #747a85;
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: grid;
  grid-template-columns: 10px 1fr auto auto;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.legend-item i {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.name {
  color: #4b5362;
}

.value {
  color: #171a20;
  font-weight: 500;
}

.percent {
  color: #7a8091;
}

@media (max-width: 768px) {
  .donut-wrap {
    grid-template-columns: 1fr;
    justify-items: center;
  }

  .legend {
    width: 100%;
  }
}
</style>
