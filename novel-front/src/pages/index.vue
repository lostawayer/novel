<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <div class="top-header">
      <div class="header-content">
        <div class="site-title">小说网站系统</div>
        <div class="user-info">
          <span v-if="userStore.isLoggedIn" class="username">{{ userStore.username }}</span>
          <el-button v-if="!userStore.isLoggedIn" @click="toLogin" size="small">
            登录/注册
          </el-button>
          <el-button v-else @click="handleLogout" size="small">退出</el-button>
        </div>
      </div>
    </div>

    <!-- 主导航菜单 -->
    <div class="main-nav">
      <el-menu
        mode="horizontal"
        :default-active="activeIndex"
        @select="handleSelect"
        :router="true"
      >
        <el-menu-item
          v-for="(menu, index) in menuList"
          :key="index"
          :index="String(index)"
          :route="menu.url"
        >
          {{ menu.name }}
        </el-menu-item>
        <el-menu-item
          v-if="userStore.isLoggedIn && userStore.role !== 'users'"
          :index="String(menuList.length)"
          @click="router.push('/index/center')"
        >
          个人中心
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 轮播图 -->
    <div class="banner-section" v-if="carouselList.length > 0">
      <el-carousel trigger="click" height="400px" :interval="5000">
        <el-carousel-item v-for="item in carouselList" :key="item.id">
          <el-image :src="getImageUrl(item.value)" fit="cover" style="width: 100%; height: 100%" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <router-view></router-view>
    </div>

    <!-- 底部 -->
    <div class="footer">
      <p>© 2024 小说网站系统 All Rights Reserved</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get } from '@/utils/request'
import { getImageUrl } from '@/common/system'
import { removeToken, removeUserInfo, removeRole } from '@/common/storage'
import { useUserStore } from '@/store'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeIndex = ref('0')
const carouselList = ref<any[]>([])
const menuList = ref<any[]>([
  { name: '首页', url: '/index/home' },
  { name: '小说列表', url: '/index/xiaoshuoxinxi' },
  { name: '公告信息', url: '/index/news' },
])

// 菜单选择
const handleSelect = (key: string, keyPath: string[]) => {
  activeIndex.value = key
}

// 登录
const toLogin = () => {
  router.push('/login')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // 清除登录信息
    removeToken()
    removeUserInfo()
    removeRole()
    userStore.clearUserInfo()

    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    // 取消退出
  }
}

// 获取轮播图
const getCarouselList = async () => {
  try {
    const res = await get('/config/list', {
      page: 1,
      limit: 10,
      sort: 'id',
    })
    if (res.code === 0 && res.data) {
      carouselList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

onMounted(() => {
  getCarouselList()
})
</script>

<style lang="scss" scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-header {
  background: #434343;
  color: #fff;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 5%;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.site-title {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;

  .username {
    margin-right: 10px;
  }
}

.main-nav {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  :deep(.el-menu) {
    border-bottom: none;
  }

  :deep(.el-menu-item) {
    font-size: 16px;
    padding: 0 30px;
  }
}

.banner-section {
  width: 80%;
  margin: 20px auto;
  border-radius: 10px;
  overflow: hidden;
}

.main-content {
  flex: 1;
  padding: 20px 0;
  background: #f5f5f5;
}

.footer {
  background: #333;
  color: #fff;
  text-align: center;
  padding: 30px 0;

  p {
    margin: 0;
    font-size: 14px;
  }
}
</style>

