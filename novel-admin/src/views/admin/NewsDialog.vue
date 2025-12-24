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

// HTML 转纯文本
function htmlToText(html: string | undefined): string {
  if (!html) return ''
  return html
    .replace(/<\/p>\s*<p>/gi, '\n\n')
    .replace(/<p>/gi, '')
    .replace(/<\/p>/gi, '')
    .replace(/<br\s*\/?>/gi, '\n')
    .trim()
}

// 纯文本转 HTML
function textToHtml(text: string | undefined): string {
  if (!text) return ''
  if (text.includes('<p>')) return text
  const paragraphs = text.split(/\n+/).filter((p) => p.trim())
  return paragraphs.map((p) => `<p>${p.trim()}</p>`).join('\n')
}

watch(
  () => props.visible,
  (val) => {
    if (val && props.data) {
      form.value = {
        id: props.data.id,
        title: props.data.title || '',
        introduction: props.data.introduction || '',
        picture: props.data.picture || '',
        content: htmlToText(props.data.content)
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
    const submitData = {
      ...form.value,
      content: textToHtml(form.value.content)
    }
    if (props.mode === 'add') {
      const res = await addNews(submitData)
      if (res.data.code === 0) {
        ElMessage.success('新增成功')
        emit('success')
      } else {
        ElMessage.error(res.data.msg || '新增失败')
      }
    } else {
      const res = await updateNews(submitData)
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
