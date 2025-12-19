<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { loginUserStore } from '@/stores/loginUser'
import { getAuthorInfoApi, updateAuthorInfoApi } from '@/api/auth/authApi'

const userStore = loginUserStore()
const loading = ref(false)
const submitting = ref(false)
const formRef = ref()

const model = ref<any>({})

const rules = {
  authorName: [{ required: true, message: '请输入作者姓名', trigger: 'blur' }]
}

async function loadUserInfo() {
  loading.value = true
  try {
    model.value = await getAuthorInfoApi(userStore.account)
  } catch (error: any) {
    ElMessage.error(error.message || '获取用户信息失败')
  } finally {
    loading.value = false
  }
}

async function submitForm() {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitting.value = true
    try {
      await updateAuthorInfoApi(model.value)
      ElMessage.success('保存成功')
      // 更新 store 中的用户名
      userStore.currentUser.authorName = model.value.authorName
    } catch (error: any) {
      ElMessage.error(error.message || '保存失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <span class="title">个人中心</span>
    </template>

    <el-form
      ref="formRef"
      :model="model"
      :rules="rules"
      label-width="100px"
      style="max-width: 500px"
    >
      <el-form-item label="账号">
        <el-input :model-value="model.account" disabled />
      </el-form-item>
      <el-form-item label="作者姓名" prop="authorName">
        <el-input v-model="model.authorName" placeholder="请输入作者姓名" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="model.gender">
          <el-radio value="男">男</el-radio>
          <el-radio value="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="头像URL" prop="avatar">
        <el-input v-model="model.avatar" placeholder="请输入头像URL" />
      </el-form-item>
      <el-form-item label="手机" prop="phone">
        <el-input v-model="model.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="model.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
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
