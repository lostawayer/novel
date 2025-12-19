<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { addAuthor, updateAuthor, type Author } from '@/api/admin/authorApi'

const props = defineProps<{
  visible: boolean
  mode: 'add' | 'edit' | 'view'
  data: Author | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const formRef = ref()
const loading = ref(false)
const form = ref({
  id: 0,
  account: '',
  password: '',
  authorName: '',
  gender: '男',
  avatar: '',
  age: '',
  idCard: '',
  phone: '',
  email: ''
})

const title = computed(() => {
  const titles = { add: '新增作者', edit: '编辑作者', view: '查看作者' }
  return titles[props.mode]
})

const isView = computed(() => props.mode === 'view')

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  authorName: [{ required: true, message: '请输入作者姓名', trigger: 'blur' }]
}

watch(
  () => props.visible,
  (val) => {
    if (val && props.data) {
      form.value = {
        id: props.data.ID,
        account: props.data.ACCOUNT || '',
        password: '',
        authorName: props.data.AUTHOR_NAME || '',
        gender: props.data.GENDER || '男',
        avatar: props.data.AVATAR || '',
        age: props.data.AGE || '',
        idCard: props.data.ID_CARD || '',
        phone: props.data.PHONE || '',
        email: props.data.EMAIL || ''
      }
    } else if (val) {
      form.value = {
        id: 0,
        account: '',
        password: '',
        authorName: '',
        gender: '男',
        avatar: '',
        age: '',
        idCard: '',
        phone: '',
        email: ''
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
      const res = await addAuthor(form.value)
      if (res.data.code === 0) {
        ElMessage.success('新增成功')
        emit('success')
      } else {
        ElMessage.error(res.data.msg || '新增失败')
      }
    } else {
      const res = await updateAuthor(form.value)
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
      <el-form-item label="账号" prop="account">
        <el-input v-model="form.account" :disabled="mode !== 'add'" />
      </el-form-item>
      <el-form-item v-if="mode === 'add'" label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password />
      </el-form-item>
      <el-form-item label="作者姓名" prop="authorName">
        <el-input v-model="form.authorName" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio value="男">男</el-radio>
          <el-radio value="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="form.age" />
      </el-form-item>
      <el-form-item label="身份证">
        <el-input v-model="form.idCard" />
      </el-form-item>
      <el-form-item label="手机">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" />
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
