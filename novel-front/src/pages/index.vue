<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <div class="top-header">
      <div class="header-content">
        <div class="site-title">文趣阁</div>
        <div class="user-info">
          <span v-if="userStore.isLoggedIn" class="username">欢迎，{{ userStore.username }}</span>
          <el-button v-if="!userStore.isLoggedIn" @click="showLogin" size="small">
            登录
          </el-button>
          <el-button v-if="!userStore.isLoggedIn" @click="showRegister" size="small" type="primary">
            注册
          </el-button>
          <el-button v-if="userStore.isLoggedIn" @click="handleLogout" size="small">退出</el-button>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <LoginDialog ref="loginDialogRef" @success="onLoginSuccess" />

    <!-- 主导航菜单 -->
    <div class="main-nav">
      <el-menu
        mode="horizontal"
        :default-active="activeIndex"
        @select="handleSelect"
        :router="true"
        :ellipsis="false"
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
          v-if="userStore.isLoggedIn"
          :index="'storeup'"
          :route="'/index/storeup'"
        >
          我的收藏
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
      <p>© 2024 文趣阁 All Rights Reserved</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get } from '@/utils/request'
import { getImageUrl } from '@/common/system'
import { removeToken, removeUserInfo, removeRole } from '@/common/storage'
import { useUserStore } from '@/store'
import LoginDialog from '@/components/LoginDialog.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeIndex = ref('0')
const carouselList = ref<any[]>([])
const loginDialogRef = ref<InstanceType<typeof LoginDialog>>()
const menuList = ref<any[]>([
  { name: '首页', url: '/index/home' },
  { name: '书籍列表', url: '/index/xiaoshuoxinxi' },
  { name: '公告信息', url: '/index/news' },
])

// 监听路由变化，如果需要登录则弹出登录框
watch(
  () => route.query.needLogin,
  (needLogin) => {
    if (needLogin === '1' && !userStore.isLoggedIn) {
      setTimeout(() => {
        loginDialogRef.value?.show('login')
      }, 100)
      // 清除query参数
      router.replace({ query: {} })
    }
  },
  { immediate: true }
)

// 菜单选择
const handleSelect = (key: string, keyPath: string[]) => {
  activeIndex.value = key
}

// 显示登录弹窗
const showLogin = () => {
  loginDialogRef.value?.show('login')
}

// 显示注册弹窗
const showRegister = () => {
  loginDialogRef.value?.show('register')
}

// 登录成功回调
const onLoginSuccess = () => {
  // 刷新当前页面数据
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
    
    // 跳转到首页
    router.push('/index/home')
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.header-content {
  width: 100%;
  max-width: 1200px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.site-title {
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 2px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .username {
    font-size: 14px;
    opacity: 0.95;
  }

  :deep(.el-button) {
    border-radius: 20px;
    padding: 8px 20px;
  }
}

.main-nav {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;

  :deep(.el-menu) {
    border-bottom: none;
    background: transparent;
    width: auto;
  }

  :deep(.el-menu-item) {
    font-size: 15px;
    padding: 0 28px;
    height: 55px;
    line-height: 55px;
    color: #333;
    transition: all 0.3s;
    
    &:hover {
      color: #667eea;
      background: rgba(102, 126, 234, 0.08);
    }
    
    &.is-active {
      color: #667eea;
      border-bottom: 3px solid #667eea;
      background: transparent;
    }
  }

  :deep(.el-sub-menu__icon-more) {
    display: none;
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

