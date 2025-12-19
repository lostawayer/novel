import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export type UserRole = 'admin' | 'author'

export interface LoginUser {
  id?: number
  account: string
  authorName: string
  avatar?: string
  isLoggedIn: boolean
  role: UserRole
  token?: string
}

export const loginUserStore = defineStore(
  'loginUser',
  () => {
    const currentUser = ref<LoginUser>({
      account: '',
      authorName: '',
      avatar: '',
      isLoggedIn: false,
      role: 'author',
      token: ''
    })

    const token = computed(() => currentUser.value.token || currentUser.value.account)
    const account = computed(() => currentUser.value.account)
    const authorName = computed(() => currentUser.value.authorName)
    const role = computed(() => currentUser.value.role)
    const isAdmin = computed(() => currentUser.value.role === 'admin')

    function login(user: LoginUser) {
      currentUser.value = user
    }

    function logout() {
      currentUser.value = {
        account: '',
        authorName: '',
        avatar: '',
        isLoggedIn: false,
        role: 'author',
        token: ''
      }
    }

    function setUserId(id: number) {
      currentUser.value.id = id
    }

    return {
      currentUser,
      token,
      account,
      authorName,
      role,
      isAdmin,
      login,
      logout,
      setUserId
    }
  },
  {
    persist: {
      storage: sessionStorage
    }
  }
)
