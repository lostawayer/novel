import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'
import { loginUserStore } from '@/stores/loginUser'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/main',
    name: 'mainLayout',
    component: () => import('@/views/MainLayout.vue'),
    redirect: '/main/welcome',
    children: [
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('@/views/Welcome.vue'),
        meta: { title: '工作台' }
      },
      // 作者功能
      {
        path: 'novelList',
        name: 'novelList',
        component: () => import('@/views/novel/NovelListView.vue'),
        meta: { title: '我的书籍' }
      },
      {
        path: 'chapterList',
        name: 'chapterList',
        component: () => import('@/views/novel/ChapterListView.vue'),
        meta: { title: '章节管理' }
      },
      // 管理员功能 - 用户管理
      {
        path: 'userList',
        name: 'userList',
        component: () => import('@/views/admin/UserListView.vue'),
        meta: { title: '用户管理', requireAdmin: true }
      },
      {
        path: 'authorList',
        name: 'authorList',
        component: () => import('@/views/admin/AuthorListView.vue'),
        meta: { title: '作者管理', requireAdmin: true }
      },
      // 管理员功能 - 书籍管理
      {
        path: 'categoryList',
        name: 'categoryList',
        component: () => import('@/views/admin/CategoryListView.vue'),
        meta: { title: '书籍类型', requireAdmin: true }
      },
      {
        path: 'bookList',
        name: 'bookList',
        component: () => import('@/views/admin/BookListView.vue'),
        meta: { title: '书籍信息', requireAdmin: true }
      },
      {
        path: 'chapterManage',
        name: 'chapterManage',
        component: () => import('@/views/admin/ChapterManageView.vue'),
        meta: { title: '章节管理', requireAdmin: true }
      },
      {
        path: 'commentList',
        name: 'commentList',
        component: () => import('@/views/admin/CommentListView.vue'),
        meta: { title: '评论管理', requireAdmin: true }
      },
      // 管理员功能 - 留言板
      {
        path: 'messageList',
        name: 'messageList',
        component: () => import('@/views/admin/MessageListView.vue'),
        meta: { title: '留言板管理', requireAdmin: true }
      },
      // 管理员功能 - 系统管理
      {
        path: 'configList',
        name: 'configList',
        component: () => import('@/views/admin/ConfigListView.vue'),
        meta: { title: '轮播图管理', requireAdmin: true }
      },
      {
        path: 'newsList',
        name: 'newsList',
        component: () => import('@/views/admin/NewsListView.vue'),
        meta: { title: '公告信息', requireAdmin: true }
      },
      {
        path: 'orderList',
        name: 'orderList',
        component: () => import('@/views/admin/OrderListView.vue'),
        meta: { title: '订单管理', requireAdmin: true }
      },
      // 通用功能
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/ProfileView.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'password',
        name: 'password',
        component: () => import('@/views/PasswordView.vue'),
        meta: { title: '修改密码' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to) => {
  // 设置页面标题
  document.title = `${to.meta.title || '“文趣阁”后台系统'} - 书籍网站后台`

  // 登录和注册页面不需要验证
  if (to.name === 'login' || to.name === 'register') {
    return true
  }

  // 验证登录状态
  const userStore = loginUserStore()
  if (!userStore.currentUser.isLoggedIn) {
    return { name: 'login' }
  }

  // 验证管理员权限
  if (to.meta.requireAdmin && !userStore.isAdmin) {
    return { name: 'welcome' }
  }

  return true
})

export default router
