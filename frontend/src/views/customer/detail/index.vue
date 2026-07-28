<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $route.meta.title }}</h2>
    </div>
    <el-empty description="页面开发中，敬请期待" />
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}
.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}
</style>
