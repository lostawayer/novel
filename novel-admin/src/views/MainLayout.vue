<script setup lang="ts">
import { RouterView, useRouter, useRoute } from 'vue-router'
import { loginUserStore } from '@/stores/loginUser'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Histogram,
  Reading,
  Expand,
  Fold,
  ArrowDown,
  SwitchButton,
  Lock,
  User,
  Collection,
  ChatDotRound,
  Bell,
  Setting,
  Document,
  Comment
} from '@element-plus/icons-vue'

const userStore = loginUserStore()
const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

// 作者菜单
const authorMenuItems = [
  { index: '/main/welcome', icon: Histogram, title: '工作台' },
  { index: '/main/novelList', icon: Reading, title: '我的小说' }
]

// 管理员菜单 - 分组 (完整功能)
const adminMenuGroups = [
  {
    title: '工作台',
    items: [{ index: '/main/welcome', icon: Histogram, title: '工作台' }]
  },
  {
    title: '用户管理',
    items: [
      { index: '/main/userList', icon: User, title: '用户管理' },
      { index: '/main/authorList', icon: User, title: '作者管理' }
    ]
  },
  {
    title: '小说类型管理',
    items: [{ index: '/main/categoryList', icon: Collection, title: '小说类型' }]
  },
  {
    title: '小说信息管理',
    items: [
      { index: '/main/bookList', icon: Reading, title: '小说信息' },
      { index: '/main/commentList', icon: Comment, title: '评论管理' }
    ]
  },
  {
    title: '留言板管理',
    items: [{ index: '/main/messageList', icon: ChatDotRound, title: '留言板管理' }]
  },
  {
    title: '系统管理',
    items: [
      { index: '/main/configList', icon: Setting, title: '轮播图管理' },
      { index: '/main/newsList', icon: Bell, title: '公告信息' }
    ]
  }
]

// 根据角色显示菜单
const menuItems = computed(() => {
  if (userStore.isAdmin) {
    return adminMenuGroups.flatMap((g) => g.items)
  }
  return authorMenuItems
})

const menuGroups = computed(() => {
  return userStore.isAdmin ? adminMenuGroups : null
})

async function onCommand(command: string) {
  switch (command) {
    case 'logout':
      userStore.logout()
      ElMessage.success('退出成功')
      await router.push({ name: 'login' })
      break
    case 'profile':
      await router.push({ name: 'profile' })
      break
    case 'password':
      await router.push({ name: 'password' })
      break
  }
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<template>
  <el-container class="main-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo-container">
        <transition name="logo">
          <div v-if="!isCollapse" class="logo-content">
            <span class="logo-text">小说管理系统</span>
          </div>
          <div v-else class="logo-mini">
            <span class="logo-text-mini">小说</span>
          </div>
        </transition>
      </div>

      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <!-- 管理员分组菜单 -->
          <template v-if="menuGroups">
            <template v-for="group in menuGroups" :key="group.title">
              <div v-if="!isCollapse" class="menu-group-title">{{ group.title }}</div>
              <el-menu-item v-for="item in group.items" :key="item.index" :index="item.index">
                <el-icon><component :is="item.icon" /></el-icon>
                <template #title>{{ item.title }}</template>
              </el-menu-item>
            </template>
          </template>
          <!-- 作者普通菜单 -->
          <template v-else>
            <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
              <el-icon><component :is="item.icon" /></el-icon>
              <template #title>{{ item.title }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>

      <!-- 角色标识 -->
      <div class="role-badge">
        <el-tag :type="userStore.isAdmin ? 'danger' : 'success'" size="small">
          {{ userStore.isAdmin ? '管理员' : '作者' }}
        </el-tag>
      </div>

      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon>
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="content-container">
      <!-- 顶部导航栏 -->
      <el-header class="navbar">
        <div class="navbar-left">
          <span class="current-page">{{ route.meta.title || '工作台' }}</span>
        </div>

        <div class="navbar-right">
          <el-dropdown trigger="click" @command="onCommand">
            <div class="user-dropdown">
              <el-avatar class="user-avatar" :size="36">
                {{ userStore.authorName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.authorName }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <div class="content-wrapper">
          <RouterView />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.main-container {
  width: 100vw;
  height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  background: linear-gradient(180deg, #304156 0%, #1f2d3d 100%);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-content,
.logo-mini {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 2px;
}

.logo-text-mini {
  font-size: 16px;
  font-weight: 600;
}

.menu-scrollbar {
  flex: 1;
  overflow-y: auto;
}

.menu-group-title {
  padding: 12px 20px 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.sidebar-menu {
  border: none;
  background: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.1);
  --el-menu-text-color: rgba(255, 255, 255, 0.8);
  --el-menu-active-color: #409eff;
  --el-menu-item-height: 50px;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.2) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #409eff 0%, #36a3f7 100%) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.role-badge {
  padding: 10px;
  display: flex;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-btn {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #409eff;
}

.content-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px !important;
}

.current-page {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: #303133;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.content-wrapper {
  min-height: 100%;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
