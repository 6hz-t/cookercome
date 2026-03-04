<template>
  <div class="page1">
    <h1>Page 1</h1>
    <p>CookerHome</p>
    <router-link to="/">首页</router-link>
  </div>
</template>

<style scoped>
.page1 {
  padding: 2rem;
  text-align: center;
}

.page1 h1 {
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.page1 a {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: var(--color-background-soft);
  border-radius: 4px;
  text-decoration: none;
}
</style>