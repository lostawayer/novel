<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { loginUserStore } from '@/stores/loginUser'
import { Reading, Edit, ArrowRight, User, UserFilled, ChatDotRound, Bell, Collection } from '@element-plus/icons-vue'
import { getNovelsByAuthorApi, type Novel } from '@/api/novel/novelApi'
import request from '@/api/request'

const router = useRouter()
const userStore = loginUserStore()
const loading = ref(false)
const novels = ref<Novel[]>([])

// 管理员统计数据
const adminStats = ref({
  userCount: 0,
  authorCount: 0,
  novelCount: 0,
  commentCount: 0
})

// 作者快捷操作
const authorQuickActions = [
  { title: '我的书籍', desc: '管理我的作品', icon: Reading, route: '/main/novelList', color: '#409eff' },
  { title: '个人中心', desc: '修改个人信息', icon: User, route: '/main/profile', color: '#67c23a' }
]

// 管理员快捷操作
const adminQuickActions = [
  { title: '用户管理', desc: '管理读者用户', icon: UserFilled, route: '/main/userList', color: '#409eff' },
  { title: '作者管理', desc: '管理作者账号', icon: User, route: '/main/authorList', color: '#67c23a' },
  { title: '书籍管理', desc: '管理所有书籍', icon: Reading, route: '/main/bookList', color: '#e6a23c' },
  { title: '评论管理', desc: '管理书籍评论', icon: ChatDotRound, route: '/main/commentList', color: '#f56c6c' },
  { title: '公告管理', desc: '发布系统公告', icon: Bell, route: '/main/newsList', color: '#909399' },
  { title: '类型管理', desc: '管理书籍分类', icon: Collection, route: '/main/categoryList', color: '#9c27b0' }
]

const quickActions = computed(() => userStore.isAdmin ? adminQuickActions : authorQuickActions)

async function loadNovels() {
  if (userStore.isAdmin) return
  loading.value = true
  try {
    novels.value = await getNovelsByAuthorApi(userStore.account)
  } catch (error) {
    console.error('获取书籍列表失败', error)
  } finally {
    loading.value = false
  }
}

async function loadAdminStats() {
  if (!userStore.isAdmin) return
  loading.value = true
  try {
    const [users, authors, novels, comments] = await Promise.all([
      request.get('/yonghu/page', { params: { page: 1, limit: 1 } }),
      request.get('/zuozhe/page', { params: { page: 1, limit: 1 } }),
      request.get('/xiaoshuoxinxi/page', { params: { page: 1, limit: 1 } }),
      request.get('/discussxiaoshuoxinxi/page', { params: { page: 1, limit: 1 } })
    ])
    adminStats.value = {
      userCount: users.data?.data?.total || 0,
      authorCount: authors.data?.data?.total || 0,
      novelCount: novels.data?.data?.total || 0,
      commentCount: comments.data?.data?.total || 0
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  } finally {
    loading.value = false
  }
}

function navigateTo(route: string) {
  router.push(route)
}

onMounted(() => {
  if (userStore.isAdmin) {
    loadAdminStats()
  } else {
    loadNovels()
  }
})
</script>

<template>
  <div class="welcome-container" v-loading="loading">
    <!-- 欢迎信息 -->
    <div class="welcome-header">
      <div class="welcome-title">
        <h2>欢迎使用“文趣阁”后台系统</h2>
        <p>{{ userStore.authorName }}，{{ userStore.isAdmin ? '系统运行正常！' : '祝您创作愉快！' }}</p>
      </div>
      <div class="header-date">
        {{ new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }) }}
      </div>
    </div>

    <!-- 管理员统计卡片 -->
    <el-row v-if="userStore.isAdmin" :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="28"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ adminStats.userCount }}</div>
              <div class="stat-title">用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ adminStats.authorCount }}</div>
              <div class="stat-title">作者数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ adminStats.novelCount }}</div>
              <div class="stat-title">书籍数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="28"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ adminStats.commentCount }}</div>
              <div class="stat-title">评论数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 作者统计卡片 -->
    <el-row v-else :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ novels.length }}</div>
              <div class="stat-title">我的书籍</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="28"><Edit /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ novels.reduce((sum, n) => sum + (n.clickCount || 0), 0) }}</div>
              <div class="stat-title">总点击量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card shadow="hover" class="quick-card">
      <template #header>
        <span class="card-title">快捷操作</span>
      </template>
      <div class="quick-actions">
        <div v-for="action in quickActions" :key="action.route" class="action-item" @click="navigateTo(action.route)">
          <div class="action-icon" :style="{ background: action.color }">
            <el-icon :size="22"><component :is="action.icon" /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">{{ action.title }}</div>
            <div class="action-desc">{{ action.desc }}</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 作者的书籍列表 -->
    <el-card v-if="!userStore.isAdmin && novels.length > 0" shadow="hover" class="novels-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">我的书籍</span>
          <el-button type="primary" link @click="navigateTo('/main/novelList')">查看全部</el-button>
        </div>
      </template>
      <div class="novels-list">
        <div v-for="novel in novels.slice(0, 5)" :key="novel.id" class="novel-item">
          <div class="novel-info">
            <span class="novel-name">{{ novel.bookName }}</span>
            <el-tag size="small" type="info">{{ novel.categoryName }}</el-tag>
          </div>
          <span class="novel-clicks">{{ novel.clickCount || 0 }} 点击</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.welcome-container {
  padding: 0;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.welcome-title h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-title p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.header-date {
  font-size: 14px;
  opacity: 0.9;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.quick-card,
.novels-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.action-item:hover {
  background: #ecf5ff;
  border-color: #409eff;
  transform: translateX(6px);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: #909399;
}

.action-arrow {
  color: #c0c4cc;
  transition: all 0.3s;
}

.action-item:hover .action-arrow {
  color: #409eff;
  transform: translateX(4px);
}

.novels-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.novel-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.novel-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.novel-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.novel-clicks {
  font-size: 12px;
  color: #909399;
}
</style>
