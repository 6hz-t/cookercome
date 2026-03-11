<script setup>
// 不需要手动导入 Home 组件，Vue Router 会自动处理
</script>

<template>
  <div id="app">
    <router-view v-slot="{ Component, route }">
      <!-- 服务中心不使用全局过渡动画，让它使用自己的内部动画 -->
      <transition :name="route.meta.transition || 'page'" mode="out-in" v-if="!route.path.startsWith('/service')">
        <component :is="Component" :key="route.path" class="page-component" />
      </transition>
      <!-- 服务中心页面直接渲染 -->
      <component :is="Component" :key="route.path" class="page-component" v-else />
    </router-view>
  </div>
</template>

<style>
/* 全局样式已在 base.css 中定义 */

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.4s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateX(-100px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(100px);
}

/* 从左到右滑入（登录页面） */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.4s ease;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(-100%);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

/* 从右到左滑入（注册页面） */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.4s ease;
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}

/* 确保页面组件正常显示 */
.page-component {
  width: 100%;
  height: 100%;
}
</style>
