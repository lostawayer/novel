<template>
  <div class="news-container">
    <div class="news-header">
      <h2>📢 公告信息</h2>
      <p class="subtitle">了解平台最新动态</p>
    </div>

    <div class="news-list" v-loading="loading">
      <div 
        v-for="item in newsList" 
        :key="item.id" 
        class="news-item"
        @click="goDetail(item.id)"
      >
        <div class="news-image" v-if="item.picture">
          <el-image :src="getImageUrl(item.picture)" fit="cover" />
        </div>
        <div class="news-content">
          <h3 class="news-title">{{ item.title }}</h3>
          <p class="news-intro">{{ item.introduction || '暂无简介' }}</p>
          <div class="news-meta">
            <span class="news-time">
              <el-icon><Clock /></el-icon>
              {{ formatDate(item.addtime) }}
            </span>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && newsList.length === 0" description="暂无公告信息" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { getImageUrl } from '@/common/system'
import { Clock } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const newsList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 格式化日期
function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取公告列表
async function getNewsList() {
  loading.value = true
  try {
    const res = await get('/api/news/page', {
      page: currentPage.value,
      limit: pageSize.value
    })
    console.log('公告列表响应:', res)
    if (res.code === 0 && res.data) {
      newsList.value = res.data.list || []
      total.value = res.data.total || 0
    } else if (res.data) {
      // 兼容直接返回data的情况
      newsList.value = res.data.list || res.data || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转详情
function goDetail(id: number) {
  router.push({ path: '/index/newsDetail', query: { id } })
}

// 分页变化
function handlePageChange(page: number) {
  currentPage.value = page
  getNewsList()
}

onMounted(() => {
  getNewsList()
})
</script>

<style scoped lang="scss">
.news-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.news-header {
  text-align: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: 28px;
    color: #333;
    margin-bottom: 8px;
  }
  
  .subtitle {
    color: #999;
    font-size: 14px;
  }
}

.news-list {
  min-height: 300px;
}

.news-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
}

.news-image {
  width: 180px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  
  :deep(.el-image) {
    width: 100%;
    height: 100%;
  }
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px;
  line-height: 1.4;
  
  &:hover {
    color: #409eff;
  }
}

.news-intro {
  flex: 1;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  margin-top: 12px;
  display: flex;
  align-items: center;
}

.news-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
