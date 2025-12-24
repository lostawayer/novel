/**
 * Pinia Store
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'
import { getUserInfo, setUserInfo as saveUserInfo, removeUserInfo, getRole, setRole as saveRole, removeRole } from '@/common/storage'

/**
 * 应用状态 Store
 */
export const useAppStore = defineStore('app', () => {
  // 侧边栏折叠状态
  const sidebarCollapsed = ref(false)

  // 切换侧边栏
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 设置侧边栏状态
  const setSidebarCollapsed = (collapsed: boolean) => {
    sidebarCollapsed.value = collapsed
  }

  return {
    sidebarCollapsed,
    toggleSidebar,
    setSidebarCollapsed,
  }
})

/**
 * 用户状态 Store
 */
export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref<User | null>(getUserInfo())

  // 用户角色
  const role = ref<string>(getRole() || '')

  // 是否登录
  const isLoggedIn = computed(() => !!userInfo.value)

  // 用户名（显示用的姓名）
  const username = computed(() => userInfo.value?.xingming || userInfo.value?.yonghuming || '')

  // 设置用户信息
  const setUserInfo = (info: User) => {
    userInfo.value = info
    saveUserInfo(info)
  }

  // 设置角色
  const setRole = (r: string) => {
    role.value = r
    saveRole(r)
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = null
    removeUserInfo()
    removeRole()
    role.value = ''
  }

  // 更新用户余额
  const updateMoney = (money: number) => {
    if (userInfo.value) {
      userInfo.value.money = money
      saveUserInfo(userInfo.value)
    }
  }

  return {
    userInfo,
    role,
    isLoggedIn,
    username,
    setUserInfo,
    setRole,
    clearUserInfo,
    updateMoney,
  }
})

