<template>
  <div class="rating-wrap">
    <div class="head">
      <strong>{{ goodRate }}%</strong>
      <span>好评率</span>
    </div>

    <div class="bars">
      <div v-for="item in data" :key="item.star" class="bar-item">
        <span class="label">{{ item.star }} 星</span>
        <div class="bar-bg">
          <div class="bar-fill" :style="{ width: `${item.percent}%` }" />
        </div>
        <span class="value">{{ item.count }} ({{ item.percent }}%)</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  data: {
    type: Array,
    default: () => []
  },
  goodRate: {
    type: Number,
    default: 0
  }
})
</script>

<style scoped>
.rating-wrap {
  display: grid;
  gap: 16px;
}

.head {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.head strong {
  font-size: 30px;
  color: #23a36f;
}

.head span {
  color: #6d7480;
}

.bars {
  display: grid;
  gap: 12px;
}

.bar-item {
  display: grid;
  grid-template-columns: 52px 1fr 100px;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.label {
  color: #4e5564;
}

.bar-bg {
  width: 100%;
  height: 8px;
  border-radius: 999px;
  background: #eceff4;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #3d72ff 0%, #2ab4ff 100%);
}

.value {
  color: #707889;
  text-align: right;
}

@media (max-width: 768px) {
  .bar-item {
    grid-template-columns: 46px 1fr 82px;
    font-size: 12px;
  }
}
</style>
