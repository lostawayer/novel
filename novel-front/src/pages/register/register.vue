<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="100px"
        size="default"
      >
        <!-- 用户注册 -->
        <template v-if="tableName === 'yonghu'">
          <el-form-item label="用户名" prop="yonghuming">
            <el-input v-model="registerForm.yonghuming" placeholder="请输入用户名" />
          </el-form-item>

          <el-form-item label="密码" prop="mima">
            <el-input
              v-model="registerForm.mima"
              type="password"
              placeholder="请输入密码"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="mima2">
            <el-input
              v-model="registerForm.mima2"
              type="password"
              placeholder="请再次输入密码"
            />
          </el-form-item>

          <el-form-item label="昵称" prop="nicheng">
            <el-input v-model="registerForm.nicheng" placeholder="请输入昵称" />
          </el-form-item>

          <el-form-item label="姓名" prop="xingming">
            <el-input v-model="registerForm.xingming" placeholder="请输入姓名" />
          </el-form-item>

          <el-form-item label="性别" prop="xingbie">
            <el-select v-model="registerForm.xingbie" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>

          <el-form-item label="头像" prop="touxiang">
            <FileUpload
              tip="点击上传头像"
              action="/file/upload"
              :limit="1"
              :multiple="false"
              :fileUrls="registerForm.touxiang"
              @change="(val: string) => (registerForm.touxiang = val)"
            />
          </el-form-item>

          <el-form-item label="邮箱" prop="youxiang">
            <el-input v-model="registerForm.youxiang" placeholder="请输入邮箱" />
          </el-form-item>

          <el-form-item label="手机" prop="shouji">
            <el-input v-model="registerForm.shouji" placeholder="请输入手机号" />
          </el-form-item>
        </template>

        <!-- 作者注册 -->
        <template v-if="tableName === 'zuozhe'">
          <el-form-item label="账号" prop="zhanghao">
            <el-input v-model="registerForm.zhanghao" placeholder="请输入账号" />
          </el-form-item>

          <el-form-item label="密码" prop="mima">
            <el-input
              v-model="registerForm.mima"
              type="password"
              placeholder="请输入密码"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="mima2">
            <el-input
              v-model="registerForm.mima2"
              type="password"
              placeholder="请再次输入密码"
            />
          </el-form-item>

          <el-form-item label="作者姓名" prop="zuozhexingming">
            <el-input v-model="registerForm.zuozhexingming" placeholder="请输入作者姓名" />
          </el-form-item>

          <el-form-item label="性别" prop="xingbie">
            <el-select v-model="registerForm.xingbie" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>

          <el-form-item label="头像" prop="touxiang">
            <FileUpload
              tip="点击上传头像"
              action="/file/upload"
              :limit="1"
              :multiple="false"
              :fileUrls="registerForm.touxiang"
              @change="(val: string) => (registerForm.touxiang = val)"
            />
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">
            注 册
          </el-button>
          <el-button @click="router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { post } from '@/utils/request'
import FileUpload from '@/components/FileUpload.vue'

const router = useRouter()
const route = useRoute()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const tableName = ref('')

const registerForm = reactive<Record<string, any>>({
  yonghuming: '',
  mima: '',
  mima2: '',
  nicheng: '',
  xingming: '',
  xingbie: '',
  touxiang: '',
  youxiang: '',
  shouji: '',
  zhanghao: '',
  zuozhexingming: '',
})

// 密码确认验证
const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.mima) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  yonghuming: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  zhanghao: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
  mima2: [{ required: true, validator: validatePass2, trigger: 'blur' }],
  xingbie: [{ required: true, message: '请选择性别', trigger: 'change' }],
}

// 注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await post(`/${tableName.value}/register`, registerForm)

        if (res.code === 0) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        }
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  tableName.value = (route.query.role as string) || 'yonghu'
})
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

:deep(.el-select) {
  width: 100%;
}
</style>

