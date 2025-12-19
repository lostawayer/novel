<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginUserStore } from '@/stores/loginUser'
import { updateAuthorInfoApi, getAuthorInfoApi } from '@/api/auth/authApi'

const router = useRouter()
const userStore = loginUserStore()
const formRef = ref()
const submitting = ref(false)

const model = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== model.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 3, message: '密码长度不能少于3位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function submitForm() {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitting.value = true
    try {
      // 获取当前用户信息
      const author = await getAuthorInfoApi(userStore.account)
      // 更新密码
      await updateAuthorInfoApi({
        ...author,
        password: model.value.newPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      router.push({ name: 'login' })
    } catch (error: any) {
      ElMessage.error(error.message || '修改失败')
    } finally {
      submitting.value = false
    }
  })
}

function resetForm() {
  model.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  formRef.value?.resetFields()
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <span class="title">修改密码</span>
    </template>

    <el-form
      ref="formRef"
      :model="model"
      :rules="rules"
      label-width="100px"
      style="max-width: 400px"
    >
      <el-form-item label="原密码" prop="oldPassword">
        <el-input
          v-model="model.oldPassword"
          type="password"
          placeholder="请输入原密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="model.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="model.confirmPassword"
          type="password"
          placeholder="请确认新密码"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="submitForm">确认修改</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.title {
  font-size: 16px;
  font-weight: 600;
}
</style>
