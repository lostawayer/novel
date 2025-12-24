<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Message } from '@element-plus/icons-vue'
import { registerAuthorApi } from '@/api/author/authorApi'
import { checkAccountExistsApi } from '@/api/auth/authApi'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const model = ref({
  account: '',
  password: '',
  confirmPassword: '',
  authorName: '',
  gender: '男',
  phone: '',
  email: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== model.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAccount = async (rule: any, value: string, callback: any) => {
  if (value) {
    try {
      const exists = await checkAccountExistsApi(value)
      if (exists) {
        callback(new Error('账号已存在'))
      } else {
        callback()
      }
    } catch {
      callback()
    }
  } else {
    callback()
  }
}

const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在3-20个字符', trigger: 'blur' },
    { validator: validateAccount, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, message: '密码长度不能少于3位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  authorName: [{ required: true, message: '请输入作者姓名', trigger: 'blur' }]
}

async function onSubmit() {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loading.value = true
    try {
      await registerAuthorApi({
        account: model.value.account,
        password: model.value.password,
        authorName: model.value.authorName,
        gender: model.value.gender,
        phone: model.value.phone,
        email: model.value.email
      })
      ElMessage.success('注册成功！您的账号正在等待管理员审核，审核通过后即可登录。')
      router.push({ name: 'login' })
    } catch (error: any) {
      ElMessage.error(error.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}

function goLogin() {
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="register-container">
    <div class="register-background">
      <div class="gradient-overlay"></div>
    </div>

    <div class="register-box">
      <div class="register-form-container">
        <div class="logo-container">
          <h2 class="welcome-text">作者注册</h2>
          <h3 class="system-title">加入我们，开始创作之旅</h3>
        </div>

        <el-alert 
          type="info" 
          :closable="false" 
          style="margin-bottom: 20px;"
        >
          <template #title>
            <span>注册后需等待管理员审核，审核通过后方可登录</span>
          </template>
        </el-alert>

        <el-form
          ref="formRef"
          :model="model"
          :rules="rules"
          class="register-form"
          label-width="0"
        >
          <el-form-item prop="account">
            <el-input v-model="model.account" placeholder="请输入账号" size="large">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="model.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="model.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="authorName">
            <el-input v-model="model.authorName" placeholder="请输入作者姓名" size="large">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="gender">
            <el-radio-group v-model="model.gender" style="width: 100%">
              <el-radio value="男">男</el-radio>
              <el-radio value="女">女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item prop="phone">
            <el-input v-model="model.phone" placeholder="请输入手机号（选填）" size="large">
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input v-model="model.email" placeholder="请输入邮箱（选填）" size="large">
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-button
            :loading="loading"
            type="primary"
            class="register-button"
            size="large"
            @click="onSubmit"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>

          <div class="login-link">
            <span>已有账号？</span>
            <el-button type="primary" link @click="goLogin">立即登录</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.register-background {
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

.register-box {
  position: relative;
  width: 90%;
  max-width: 500px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  background-color: #fff;
  z-index: 1;
}

.register-form-container {
  padding: 50px;
}

.logo-container {
  margin-bottom: 30px;
  text-align: center;
}

.welcome-text {
  font-size: 28px;
  margin-bottom: 10px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.system-title {
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

.register-form :deep(.el-input__wrapper) {
  padding: 10px 15px;
  border-radius: 10px;
}

.register-button {
  width: 100%;
  height: 45px;
  border-radius: 22px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #909399;
}
</style>
