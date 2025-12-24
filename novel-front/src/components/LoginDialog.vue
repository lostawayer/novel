<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="480px"
    :close-on-click-modal="false"
    destroy-on-close
    class="auth-dialog"
  >
    <!-- 登录表单 -->
    <el-form 
      v-if="currentMode === 'login'" 
      ref="loginFormRef" 
      :model="loginForm" 
      :rules="loginRules" 
      size="large"
      @keyup.enter="handleLogin"
    >
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="请输入账户"
          :prefix-icon="User"
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="handleLogin">
          登 录
        </el-button>
      </el-form-item>

      <div class="switch-mode">
        还没有账号？<span @click="switchMode('register')">注册读者</span>
        <span class="divider">|</span>
        <span @click="switchMode('authorRegister')">注册作者</span>
      </div>
    </el-form>

    <!-- 读者注册表单 -->
    <el-form 
      v-else-if="currentMode === 'register'" 
      ref="registerFormRef" 
      :model="registerForm" 
      :rules="registerRules" 
      size="large"
      label-position="top"
    >
      <el-form-item label="账户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入账户名" :prefix-icon="User" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请再次输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="registerForm.nickname" placeholder="请输入昵称" :prefix-icon="UserFilled" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="registerForm.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="handleRegister">
          注 册
        </el-button>
      </el-form-item>

      <div class="switch-mode">
        已有账号？<span @click="switchMode('login')">立即登录</span>
        <span class="divider">|</span>
        <span @click="switchMode('authorRegister')">注册作者</span>
      </div>
    </el-form>

    <!-- 作者注册表单 -->
    <el-form 
      v-else-if="currentMode === 'authorRegister'" 
      ref="authorRegisterFormRef" 
      :model="authorRegisterForm" 
      :rules="authorRegisterRules" 
      size="large"
      label-position="top"
    >
      <el-alert 
        type="info" 
        :closable="false" 
        style="margin-bottom: 16px;"
      >
        <template #title>
          <span>作者注册后需等待管理员审核，审核通过后可登录后台管理系统发布书籍</span>
        </template>
      </el-alert>

      <el-form-item label="账户名" prop="username">
        <el-input v-model="authorRegisterForm.username" placeholder="请输入账户名" :prefix-icon="User" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="authorRegisterForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="authorRegisterForm.confirmPassword"
          type="password"
          placeholder="请再次输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <el-form-item label="笔名" prop="authorName">
        <el-input v-model="authorRegisterForm.authorName" placeholder="请输入您的笔名" :prefix-icon="Edit" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="authorRegisterForm.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="authorRegisterForm.email" placeholder="请输入邮箱" :prefix-icon="Message" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="handleAuthorRegister">
          提交申请
        </el-button>
      </el-form-item>

      <div class="switch-mode">
        已有账号？<span @click="switchMode('login')">立即登录</span>
        <span class="divider">|</span>
        <span @click="switchMode('auditQuery')">查询审核状态</span>
      </div>
    </el-form>

    <!-- 审核状态查询 -->
    <div v-else-if="currentMode === 'auditQuery'" class="audit-query">
      <el-form 
        v-if="!auditResult"
        ref="auditQueryFormRef" 
        :model="auditQueryForm" 
        :rules="auditQueryRules" 
        size="large"
        label-position="top"
      >
        <el-form-item label="账户名" prop="username">
          <el-input v-model="auditQueryForm.username" placeholder="请输入注册时的账户名" :prefix-icon="User" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleQueryAudit">
            查询审核状态
          </el-button>
        </el-form-item>
      </el-form>

      <div v-else class="audit-result">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="账户名">{{ auditResult.username }}</el-descriptions-item>
          <el-descriptions-item label="笔名">{{ auditResult.authorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ auditResult.createTime }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="getAuditStatusType(auditResult.auditStatus)">
              {{ getAuditStatusText(auditResult.auditStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="auditResult.auditReply" label="审核回复">
            {{ auditResult.auditReply }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="audit-tips" v-if="auditResult.auditStatus === '待审核'">
          <el-alert type="info" :closable="false">
            您的账号正在审核中，请耐心等待管理员审核。审核通过后，您可以使用账号登录后台管理系统。
          </el-alert>
        </div>
        <div class="audit-tips" v-else-if="auditResult.auditStatus === '是'">
          <el-alert type="success" :closable="false">
            恭喜！您的账号已审核通过，请前往后台管理系统登录。
            <br />
            <a href="/admin" target="_blank" style="color: #67c23a;">点击进入后台管理系统</a>
          </el-alert>
        </div>
        <div class="audit-tips" v-else>
          <el-alert type="error" :closable="false">
            很抱歉，您的账号审核未通过。如有疑问，请联系管理员。
          </el-alert>
        </div>

        <el-button style="width: 100%; margin-top: 16px;" @click="auditResult = null">
          重新查询
        </el-button>
      </div>

      <div class="switch-mode" style="margin-top: 16px;">
        <span @click="switchMode('login')">返回登录</span>
        <span class="divider">|</span>
        <span @click="switchMode('authorRegister')">注册作者</span>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, UserFilled, Phone, Edit, Message } from '@element-plus/icons-vue'
import { post, get } from '@/utils/request'
import { setToken, setUserInfo, setRole, removeToken, removeUserInfo, removeRole } from '@/common/storage'
import { useUserStore } from '@/store'

const userStore = useUserStore()

const visible = ref(false)
const currentMode = ref<'login' | 'register' | 'authorRegister' | 'auditQuery'>('login')
const loading = ref(false)
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()
const authorRegisterFormRef = ref<FormInstance>()
const auditQueryFormRef = ref<FormInstance>()
const auditResult = ref<any>(null)

// 对话框标题
const dialogTitle = computed(() => {
  switch (currentMode.value) {
    case 'login': return '用户登录'
    case 'register': return '读者注册'
    case 'authorRegister': return '作者注册'
    case 'auditQuery': return '审核状态查询'
    default: return '用户登录'
  }
})

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
})

// 读者注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
})

// 作者注册表单
const authorRegisterForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  authorName: '',
  phone: '',
  email: '',
})

// 审核查询表单
const auditQueryForm = reactive({
  username: '',
})

// 验证确认密码
const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAuthorConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== authorRegisterForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 登录规则
const loginRules: FormRules = {
  username: [{ required: true, message: '请输入账户', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// 读者注册规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入账户名', trigger: 'blur' },
    { min: 3, max: 20, message: '账户名长度为3-20个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
}

// 作者注册规则
const authorRegisterRules: FormRules = {
  username: [
    { required: true, message: '请输入账户名', trigger: 'blur' },
    { min: 3, max: 20, message: '账户名长度为3-20个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateAuthorConfirmPassword, trigger: 'blur' },
  ],
  authorName: [{ required: true, message: '请输入笔名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

// 审核查询规则
const auditQueryRules: FormRules = {
  username: [{ required: true, message: '请输入账户名', trigger: 'blur' }],
}

const emit = defineEmits<{
  success: []
}>()

// 登录
async function handleLogin() {
  if (!loginFormRef.value) return

  let valid = false
  try {
    valid = await loginFormRef.value.validate()
  } catch {
    return
  }
  if (!valid) return

  loading.value = true
  try {
    const res = await post('/yonghu/login', {
      username: loginForm.username,
      password: loginForm.password,
    })

    if (res.code === 0) {
      // 先清除旧的登录信息
      removeToken()
      removeUserInfo()
      removeRole()
      userStore.clearUserInfo()
      
      // 设置新的登录信息
      setToken(res.token)
      setUserInfo(res.data)
      setRole('yonghu')

      userStore.setUserInfo(res.data)
      userStore.setRole('yonghu')

      ElMessage.success('登录成功')
      visible.value = false
      emit('success')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 读者注册
async function handleRegister() {
  console.log('handleRegister called')
  console.log('registerFormRef.value:', registerFormRef.value)
  
  if (!registerFormRef.value) {
    console.log('registerFormRef is null')
    ElMessage.warning('表单未准备好，请稍后重试')
    return
  }

  try {
    await registerFormRef.value.validate()
  } catch (e) {
    console.log('表单验证失败:', e)
    return
  }

  console.log('表单验证通过，开始注册')
  loading.value = true
  try {
    const res = await post('/yonghu/register', {
      username: registerForm.username,
      password: registerForm.password,
      yonghuxingming: registerForm.nickname,
      shouji: registerForm.phone,
    })
    console.log('注册响应:', res)

    if (res.code === 0) {
      ElMessage.success('注册成功，请登录')
      switchMode('login')
      // 自动填充账号
      loginForm.username = registerForm.username
      loginForm.password = ''
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 作者注册
async function handleAuthorRegister() {
  if (!authorRegisterFormRef.value) return

  let valid = false
  try {
    valid = await authorRegisterFormRef.value.validate()
  } catch {
    return
  }
  if (!valid) return

  loading.value = true
  try {
    const res = await post('/zuozhe/register', {
      username: authorRegisterForm.username,
      password: authorRegisterForm.password,
      authorName: authorRegisterForm.authorName,
      phone: authorRegisterForm.phone,
      email: authorRegisterForm.email,
    })
    console.log('作者注册响应:', res)

    if (res.code === 0) {
      ElMessage.success(res.msg || '注册成功，请等待审核')
      // 切换到审核查询页面
      auditQueryForm.username = authorRegisterForm.username
      switchMode('auditQuery')
      // 自动查询审核状态
      handleQueryAudit()
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 查询审核状态
async function handleQueryAudit() {
  if (!auditQueryFormRef.value && !auditQueryForm.username) return

  if (auditQueryFormRef.value) {
    const valid = await auditQueryFormRef.value.validate().catch(() => false)
    if (!valid) return
  }

  loading.value = true
  try {
    const res = await get('/zuozhe/auditStatus', { username: auditQueryForm.username })

    if (res.code === 0) {
      auditResult.value = res.data
    }
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取审核状态类型
function getAuditStatusType(status: string) {
  switch (status) {
    case '是': return 'success'
    case '待审核': return 'warning'
    default: return 'danger'
  }
}

// 获取审核状态文本
function getAuditStatusText(status: string) {
  switch (status) {
    case '是': return '已通过'
    case '待审核': return '待审核'
    default: return '未通过'
  }
}

// 切换模式
function switchMode(mode: 'login' | 'register' | 'authorRegister' | 'auditQuery') {
  currentMode.value = mode
  auditResult.value = null
  resetForms()
}

// 重置表单
function resetForms() {
  loginForm.username = ''
  loginForm.password = ''
  registerForm.username = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  registerForm.nickname = ''
  registerForm.phone = ''
  authorRegisterForm.username = ''
  authorRegisterForm.password = ''
  authorRegisterForm.confirmPassword = ''
  authorRegisterForm.authorName = ''
  authorRegisterForm.phone = ''
  authorRegisterForm.email = ''
  auditQueryForm.username = ''
}

// 打开弹窗
function show(mode: 'login' | 'register' | 'authorRegister' | 'auditQuery' = 'login') {
  resetForms()
  currentMode.value = mode
  auditResult.value = null
  visible.value = true
}

defineExpose({ show })
</script>

<style scoped lang="scss">
.auth-dialog {
  :deep(.el-dialog__header) {
    text-align: center;
    padding: 20px 20px 10px;
    border-bottom: 1px solid #f0f0f0;
  }

  :deep(.el-dialog__title) {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }

  :deep(.el-dialog__body) {
    padding: 30px 35px 10px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #333;
  }
}

.switch-mode {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 10px;

  span {
    color: #409eff;
    cursor: pointer;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }

  .divider {
    color: #ddd;
    margin: 0 10px;
    cursor: default;

    &:hover {
      text-decoration: none;
    }
  }
}

.audit-query {
  .audit-result {
    :deep(.el-descriptions) {
      margin-bottom: 16px;
    }
  }

  .audit-tips {
    margin-top: 16px;
  }
}
</style>
