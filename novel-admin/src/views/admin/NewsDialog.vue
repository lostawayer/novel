<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { addNews, updateNews, type News } from '@/api/admin/newsApi'

const props = defineProps<{
  visible: boolean
  mode: 'add' | 'edit' | 'view'
  data: News | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const formRef = ref()
const loading = ref(false)
const form = ref({
  id: 0,
  title: '',
  introduction: '',
  picture: '',
  content: ''
})

const title = computed(() => {
  const titles = { add: '新增公告', edit: '编辑公告', view: '查看公告' }
  return titles[props.mode]
})

const isView = computed(() => props.mode === 'view')

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

watch(
  () => props.visible,
  (val) => {
    if (val && props.data) {
      form.value = {
        id: props.data.id,
        title: props.data.TITLE || '',
        introduction: props.data.INTRODUCTION || '',
        picture: props.data.PICTURE || '',
        content: props.data.CONTENT || ''
      }
    } else if (val) {
      form.value = {
        id: 0,
        title: '',
        introduction: '',
        picture: '',
        content: ''
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
      const res = await addNews(form.value)
      if (res.data.code === 0) {
        ElMessage.success('新增成功')
        emit('success')
      } else {
        ElMessage.error(res.data.msg || '新增失败')
      }
    } else {
      const res = await updateNews(form.value)
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
  <el-dialog :model-value="visible" :title="title" width="700px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" :disabled="isView">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.introduction" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="图片">
        <el-input v-model="form.picture" placeholder="图片路径" />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="10" />
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
