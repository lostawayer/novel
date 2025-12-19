<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { addUser, updateUser, type Reader } from '@/api/admin/userApi'

const props = defineProps<{
  visible: boolean
  mode: 'add' | 'edit' | 'view'
  data: Reader | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const formRef = ref()
const loading = ref(false)
const form = ref({
  id: 0,
  username: '',
  password: '',
  nickname: '',
  realName: '',
  gender: '男',
  avatar: '',
  email: '',
  phone: '',
  vip: '否'
})

const title = computed(() => {
  const titles = { add: '新增用户', edit: '编辑用户', view: '查看用户' }
  return titles[props.mode]
})

const isView = computed(() => props.mode === 'view')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机', trigger: 'blur' }]
}

watch(
  () => props.visible,
  (val) => {
    if (val && props.data) {
      form.value = {
        id: props.data.id,
        username: props.data.USERNAME || '',
        password: '',
        nickname: props.data.NICKNAME || '',
        realName: props.data.REAL_NAME || '',
        gender: props.data.GENDER || '男',
        avatar: props.data.AVATAR || '',
        email: props.data.EMAIL || '',
        phone: props.data.PHONE || '',
        vip: props.data.VIP || '否'
      }
    } else if (val) {
      form.value = {
        id: 0,
        username: '',
        password: '',
        nickname: '',
        realName: '',
        gender: '男',
        avatar: '',
        email: '',
        phone: '',
        vip: '否'
      }
    }
  }
)

async function handleSubmit() {
  if (isView.value) {
    emit('update:visible', false)
    return
  }
  await formRef.value?.validate()
  loading.value = true
  try {
    if (props.mode === 'add') {
      const res = await addUser(form.value)
      if (res.data.code === 0) {
        ElMessage.success('新增成功')
        emit('success')
      } else {
        ElMessage.error(res.data.msg || '新增失败')
      }
    } else {
      const res = await updateUser(form.value)
      if (res.data.code === 0) {
        ElMessage.success('更新成功')
        emit('success')
      } else {
        ElMessage.error(res.data.msg || '更新失败')
      }
    }
  } finally {
    loading.value = false
  }
}

function handleClose() {
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog :model-value="visible" :title="title" width="600px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" :disabled="isView">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" :disabled="mode !== 'add'" />
      </el-form-item>
      <el-form-item v-if="mode === 'add'" label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" />
      </el-form-item>
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="form.realName" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio value="男">男</el-radio>
          <el-radio value="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="手机" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="VIP">
        <el-radio-group v-model="form.vip">
          <el-radio value="是">是</el-radio>
          <el-radio value="否">否</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">{{ isView ? '关闭' : '取消' }}</el-button>
      <el-button v-if="!isView" type="primary" :loading="loading" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>
