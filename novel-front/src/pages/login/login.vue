<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">小说网站系统登录</h2>

      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账户"
            prefix-icon="User"
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>

        <el-form-item v-if="roles.length > 1" prop="tableName">
          <el-radio-group v-model="loginForm.tableName">
            <el-radio v-for="item in roles" :key="item.tableName" :label="item.tableName">
              {{ item.roleName }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-links">
        <template v-for="item in roles" :key="item.tableName">
          <router-link
            v-if="item.hasFrontRegister === '是'"
            :to="{ path: '/register', query: { role: item.tableName } }"
            class="register-link"
          >
            注册{{ item.roleName }}
          </router-link>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { post } from '@/utils/request'
import { setToken, setUserInfo, setRole } from '@/common/storage'
import { useUserStore } from '@/store'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

interface RoleMenu {
  tableName: string
  roleName: string
  hasFrontLogin: string
  hasFrontRegister: string
  [key: string]: any
}

const roleMenus: RoleMenu[] = [
  {
    tableName: 'users',
    roleName: '管理员',
    hasBackLogin: '是',
    hasBackRegister: '否',
    hasFrontLogin: '否',
    hasFrontRegister: '否',
  },
  {
    tableName: 'yonghu',
    roleName: '用户',
    hasBackLogin: '否',
    hasBackRegister: '否',
    hasFrontLogin: '是',
    hasFrontRegister: '是',
  },
  {
    tableName: 'zuozhe',
    roleName: '作者',
    hasBackLogin: '是',
    hasBackRegister: '是',
    hasFrontLogin: '否',
    hasFrontRegister: '否',
  },
]

const loginForm = reactive({
  username: '',
  password: '',
  tableName: '',
})

const roles = ref<RoleMenu[]>([])

const rules: FormRules = {
  username: [{ required: true, message: '请输入账户', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// 登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await post(`/${loginForm.tableName}/login`, {
          username: loginForm.username,
          password: loginForm.password,
        })

        if (res.code === 0) {
          // 保存登录信息
          setToken(res.token)
          setUserInfo(res.data)
          setRole(loginForm.tableName)

          // 更新 store
          userStore.setUserInfo(res.data)
          userStore.setRole(loginForm.tableName)

          ElMessage.success('登录成功')

          // 跳转
          const redirect = (route.query.redirect as string) || '/index/home'
          router.push(redirect)
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  // 筛选出前台可登录的角色
  roles.value = roleMenus.filter((item) => item.hasFrontLogin === '是')
  if (roles.value.length > 0) {
    loginForm.tableName = roles.value[0].tableName
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 450px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.register-links {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.register-link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;

  &:hover {
    text-decoration: underline;
  }
}

:deep(.el-radio-group) {
  width: 100%;
  display: flex;
  justify-content: space-around;
}
</style>

