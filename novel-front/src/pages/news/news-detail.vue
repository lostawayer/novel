<template>
  <div class="news-detail-container">
    <div class="detail-card" v-loading="loading">
      <template v-if="newsInfo">
        <!-- 标题 -->
        <h1 class="news-title">{{ newsInfo.title }}</h1>
        
        <!-- 元信息 -->
        <div class="news-meta">
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            发布时间：{{ formatDate(newsInfo.addtime) }}
          </span>
        </div>

        <!-- 封面图 -->
        <div class="news-cover" v-if="newsInfo.picture">
          <el-image 
            :src="getImageUrl(newsInfo.picture)" 
            fit="cover"
            :preview-src-list="[getImageUrl(newsInfo.picture)]"
          />
        </div>

        <!-- 简介 -->
        <div class="news-intro" v-if="newsInfo.introduction">
          <p>{{ newsInfo.introduction }}</p>
        </div>

        <!-- 正文内容 -->
        <div class="news-content" v-html="newsInfo.content"></div>

        <!-- 返回按钮 -->
        <div class="back-btn">
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回公告列表
          </el-button>
        </div>
      </template>

      <el-empty v-if="!loading && !newsInfo" description="公告不存在或已删除" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { getImageUrl } from '@/common/system'
import { Clock, ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const newsInfo = ref<any>(null)

// 格式化日期
function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取公告详情
async function getNewsDetail() {
  const id = route.query.id
  if (!id) {
    return
  }
  
  loading.value = true
  try {
    const res = await get(`/api/news/info/${id}`)
    if (res.code === 0 && res.data) {
      newsInfo.value = res.data
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回列表
function goBack() {
  router.push('/index/news')
}

onMounted(() => {
  getNewsDetail()
})
</script>

<style scoped lang="scss">
.news-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.detail-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  min-height: 400px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.news-title {
  font-size: 28px;
  color: #333;
  text-align: center;
  margin: 0 0 20px;
  line-height: 1.4;
}

.news-meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #999;
}

.news-cover {
  margin-bottom: 24px;
  text-align: center;
  
  :deep(.el-image) {
    max-width: 100%;
    max-height: 400px;
    border-radius: 8px;
  }
}

.news-intro {
  background: #f8f9fa;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  border-left: 4px solid #409eff;
  
  p {
    margin: 0;
    color: #666;
    font-size: 15px;
    line-height: 1.8;
  }
}

.news-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  
  :deep(p) {
    margin: 0 0 16px;
    text-indent: 2em;
  }
  
  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
    margin: 16px 0;
  }
}

.back-btn {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}
</style>
