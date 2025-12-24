/**
 * Vue Router 配置
 */

import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/common/storage'
import { ElMessage } from 'element-plus'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/index/home',
  },
  {
    path: '/index',
    name: 'Index',
    component: () => import('@/pages/index.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/pages/home/home.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'center',
        name: 'Center',
        component: () => import('@/pages/center/center.vue'),
        meta: { title: '个人中心', requireAuth: true },
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/pages/messages/list.vue'),
        meta: { title: '消息中心', requireAuth: true },
      },
      {
        path: 'storeup',
        name: 'Storeup',
        component: () => import('@/pages/storeup/list.vue'),
        meta: { title: '我的收藏', requireAuth: true },
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/pages/news/news-list.vue'),
        meta: { title: '新闻公告' },
      },
      {
        path: 'newsDetail',
        name: 'NewsDetail',
        component: () => import('@/pages/news/news-detail.vue'),
        meta: { title: '新闻详情' },
      },
      // 用户相关
      {
        path: 'yonghu',
        name: 'YonghuList',
        component: () => import('@/pages/yonghu/list.vue'),
        meta: { title: '用户列表' },
      },
      {
        path: 'yonghuDetail',
        name: 'YonghuDetail',
        component: () => import('@/pages/yonghu/detail.vue'),
        meta: { title: '用户详情' },
      },
      {
        path: 'yonghuAdd',
        name: 'YonghuAdd',
        component: () => import('@/pages/yonghu/add.vue'),
        meta: { title: '添加用户', requireAuth: true },
      },
      // 作者相关
      {
        path: 'zuozhe',
        name: 'ZuozheList',
        component: () => import('@/pages/zuozhe/list.vue'),
        meta: { title: '作者列表' },
      },
      {
        path: 'zuozheDetail',
        name: 'ZuozheDetail',
        component: () => import('@/pages/zuozhe/detail.vue'),
        meta: { title: '作者详情' },
      },
      {
        path: 'zuozheAdd',
        name: 'ZuozheAdd',
        component: () => import('@/pages/zuozhe/add.vue'),
        meta: { title: '添加作者', requireAuth: true },
      },
      // 书籍分类
      {
        path: 'xiaoshuoleixing',
        name: 'XiaoshuoleixingList',
        component: () => import('@/pages/xiaoshuoleixing/list.vue'),
        meta: { title: '书籍分类' },
      },
      {
        path: 'xiaoshuoleixingDetail',
        name: 'XiaoshuoleixingDetail',
        component: () => import('@/pages/xiaoshuoleixing/detail.vue'),
        meta: { title: '分类详情' },
      },
      {
        path: 'xiaoshuoleixingAdd',
        name: 'XiaoshuoleixingAdd',
        component: () => import('@/pages/xiaoshuoleixing/add.vue'),
        meta: { title: '添加分类', requireAuth: true },
      },
      // 书籍信息
      {
        path: 'xiaoshuoxinxi',
        name: 'XiaoshuoxinxiList',
        component: () => import('@/pages/xiaoshuoxinxi/list.vue'),
        meta: { title: '书籍列表' },
      },
      {
        path: 'xiaoshuoxinxiDetail',
        name: 'XiaoshuoxinxiDetail',
        component: () => import('@/pages/xiaoshuoxinxi/detail.vue'),
        meta: { title: '书籍详情' },
      },
      {
        path: 'xiaoshuoxinxiAdd',
        name: 'XiaoshuoxinxiAdd',
        component: () => import('@/pages/xiaoshuoxinxi/add.vue'),
        meta: { title: '添加书籍', requireAuth: true },
      },
      {
        path: 'xiaoshuoxinxiChapter',
        name: 'XiaoshuoxinxiChapter',
        component: () => import('@/pages/xiaoshuoxinxi/chapter.vue'),
        meta: { title: '书籍章节' },
      },
    ],
  },
  // 兼容旧路由，重定向到首页
  {
    path: '/login',
    redirect: '/index/home',
  },
  {
    path: '/register',
    redirect: '/index/home',
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 文趣阁`
  }

  // 判断是否需要登录 - 未登录时重定向到首页（会自动弹出登录框）
  if (to.meta.requireAuth) {
    const token = getToken()
    if (!token) {
      ElMessage.warning('请先登录')
      next({ path: '/index/home', query: { needLogin: '1' } })
      return
    }
  }

  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
})

export default router

