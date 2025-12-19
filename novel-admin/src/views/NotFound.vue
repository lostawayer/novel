<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push({ name: 'welcome' })
}

function goBack() {
  router.back()
}
</script>

<template>
  <div class="not-found">
    <div class="content">
      <h1 class="code">404</h1>
      <h2 class="title">页面未找到</h2>
      <p class="description">抱歉，您访问的页面不存在</p>
      <div class="actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上页</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  text-align: center;
  color: #fff;
}

.code {
  font-size: 120px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.title {
  font-size: 32px;
  font-weight: 600;
  margin: 20px 0;
}

.description {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}
</style>
