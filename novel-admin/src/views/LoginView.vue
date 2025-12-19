<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Hide, View } from '@element-plus/icons-vue'
import { loginUserStore, type LoginUser, type UserRole } from '@/stores/loginUser'
import { loginApi, adminLoginApi } from '@/api/auth/authApi'

const router = useRouter()
const userStore = loginUserStore()
const loginForm = ref()
const passwordRef = ref()
const loading = ref(false)
const passwordType = ref('password')
const loginRole = ref<UserRole>('author')

interface LoginModel {
  account: string
  password: string
}

const model = ref<LoginModel>({
  account: '',
  password: ''
})

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, message: '密码长度不能少于3位', trigger: 'blur' }
  ]
}

const showPwd = () => {
  passwordType.value = passwordType.value === 'password' ? 'text' : 'password'
}

function onSubmit() {
  loginForm.value.validate((valid: boolean) => {
    if (valid) login()
  })
}

async function login() {
  loading.value = true
  try {
    if (loginRole.value === 'admin') {
      // 管理员登录
      const admin = await adminLoginApi({
        username: model.value.account,
        password: model.value.password
      })
      const loginUser: LoginUser = {
        id: admin.id,
        account: admin.username,
        authorName: admin.username,
        isLoggedIn: true,
        role: 'admin',
        token: admin.token
      }
      userStore.login(loginUser)
    } else {
      // 作者登录
      const author = await loginApi({
        account: model.value.account,
        password: model.value.password
      })
      const loginUser: LoginUser = {
        id: author.id,
        account: author.account,
        authorName: author.authorName,
        avatar: author.avatar,
        isLoggedIn: true,
        role: 'author'
      }
      userStore.login(loginUser)
    }

    ElMessage.success('登录成功！')
    await router.push({ name: 'welcome' })
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push({ name: 'register' })
}
</script>

<template>
  <div class="login-container">
    <div class="login-background">
      <div class="gradient-overlay"></div>
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>

    <div class="login-box">
      <div class="login-form-container">
        <div class="logo-container">
          <h2 class="welcome-text">欢迎回来</h2>
          <h3 class="system-title">小说网站管理系统</h3>
        </div>

        <el-form ref="loginForm" :model="model" :rules="rules" class="login-form">
          <el-form-item>
            <el-radio-group v-model="loginRole" class="role-selector">
              <el-radio-button value="author">作者登录</el-radio-button>
              <el-radio-button value="admin">管理员登录</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item prop="account">
            <el-input v-model="model.account" :placeholder="loginRole === 'admin' ? '请输入管理员账号' : '请输入作者账号'" size="large">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              ref="passwordRef"
              v-model="model.password"
              :type="passwordType"
              placeholder="请输入密码"
              size="large"
              @keyup.enter="onSubmit"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
            <span class="show-pwd" @click="showPwd">
              <el-icon v-if="passwordType === 'password'"><Hide /></el-icon>
              <el-icon v-else><View /></el-icon>
            </span>
          </el-form-item>

          <el-button
            :loading="loading"
            type="primary"
            class="login-button"
            size="large"
            @click.prevent="onSubmit"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>

          <div v-if="loginRole === 'author'" class="register-link">
            <span>还没有账号？</span>
            <el-button type="primary" link @click="goRegister">立即注册</el-button>
          </div>
        </el-form>

        <div class="login-footer">
          <p class="copyright">© 2025 小说网站管理系统</p>
        </div>
      </div>

      <div class="login-image">
        <div class="overlay">
          <h2 class="slogan">阅读 · 创作 · 分享</h2>
          <p class="description">基于 Vue3 + Element Plus 的现代化小说管理系统</p>
          <div class="features">
            <div class="feature-item">
              <el-icon size="24"><User /></el-icon>
              <span>作者管理</span>
            </div>
            <div class="feature-item">
              <el-icon size="24"><Reading /></el-icon>
              <span>小说管理</span>
            </div>
            <div class="feature-item">
              <el-icon size="24"><Edit /></el-icon>
              <span>章节编辑</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Reading, Edit } from '@element-plus/icons-vue'
export default {
  components: { Reading, Edit }
}
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.8) 0%,
    rgba(118, 75, 162, 0.9) 100%
  );
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
}
.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: 100px;
  animation-delay: 5s;
}
.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: -50px;
  animation-delay: 10s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-30px) scale(1.1);
  }
}

.login-box {
  position: relative;
  width: 90%;
  max-width: 1000px;
  height: 600px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  background-color: #fff;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-form-container {
  width: 50%;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
}

.logo-container {
  margin-bottom: 40px;
  text-align: center;
}

.welcome-text {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.system-title {
  font-size: 16px;
  color: #909399;
  font-weight: 400;
}

.login-form {
  flex: 1;
}

.login-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 10px;
}

.role-selector {
  width: 100%;
  display: flex;
}

.role-selector :deep(.el-radio-button) {
  flex: 1;
}

.role-selector :deep(.el-radio-button__inner) {
  width: 100%;
  border-radius: 8px;
}

.role-selector :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 8px 0 0 8px;
}

.role-selector :deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0 8px 8px 0;
}

.role-selector :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
}

.show-pwd {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #889aa4;
}

.login-button {
  width: 100%;
  height: 50px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-top: 10px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #909399;
}

.login-footer {
  text-align: center;
}

.copyright {
  font-size: 12px;
  color: #909399;
}

.login-image {
  width: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overlay {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.slogan {
  color: #fff;
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.description {
  color: rgba(255, 255, 255, 0.95);
  font-size: 16px;
  text-align: center;
  margin-bottom: 50px;
}

.features {
  display: flex;
  gap: 30px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #fff;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-5px);
}

@media screen and (max-width: 992px) {
  .login-box {
    flex-direction: column;
    height: auto;
  }
  .login-form-container,
  .login-image {
    width: 100%;
  }
  .login-image {
    height: 250px;
    order: -1;
  }
}
</style>
