<template>
  <div class="editor-container">
    <!-- 上传文件组件辅助 -->
    <el-upload
      ref="uploadRef"
      class="avatar-uploader"
      :action="actionUrl"
      name="file"
      :headers="headers"
      :show-file-list="false"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      :before-upload="beforeUpload"
      style="display: none"
    >
    </el-upload>

    <QuillEditor
      ref="editorRef"
      v-model:content="content"
      contentType="html"
      theme="snow"
      :options="editorOption"
      @update:content="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import type Quill from 'quill'
import { ElMessage } from 'element-plus'
import { getToken } from '@/common/storage'
import config from '@/config/config'

interface Props {
  modelValue?: string
  action?: string
  maxSize?: number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  action: '/file/upload',
  maxSize: 4000, // kb
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const uploadRef = ref()
const editorRef = ref()
const content = ref(props.modelValue)

const headers = computed(() => ({
  Token: getToken() || '',
}))

const actionUrl = computed(() => {
  return config.baseUrl + props.action
})

// 工具栏配置
const editorOption = {
  placeholder: '请输入内容',
  modules: {
    toolbar: {
      container: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ header: 1 }, { header: 2 }],
        [{ list: 'ordered' }, { list: 'bullet' }],
        [{ script: 'sub' }, { script: 'super' }],
        [{ indent: '-1' }, { indent: '+1' }],
        [{ size: ['small', false, 'large', 'huge'] }],
        [{ header: [1, 2, 3, 4, 5, 6, false] }],
        [{ color: [] }, { background: [] }],
        [{ font: [] }],
        [{ align: [] }],
        ['clean'],
        ['link', 'image', 'video'],
      ],
      handlers: {
        image: function (this: any) {
          // 触发文件选择
          const input = uploadRef.value?.$el.querySelector('input[type=file]')
          if (input) {
            input.click()
          }
        },
      },
    },
  },
}

// 上传前
const beforeUpload = (file: File) => {
  const isLt = file.size / 1024 < props.maxSize
  if (!isLt) {
    ElMessage.error(`上传图片大小不能超过 ${props.maxSize}KB!`)
  }
  return isLt
}

// 上传成功
const uploadSuccess = (response: any) => {
  if (response.code === 0) {
    // 获取编辑器实例
    const quill = editorRef.value?.getQuill() as Quill
    if (quill) {
      // 获取光标位置
      const range = quill.getSelection()
      const index = range ? range.index : 0
      // 插入图片
      const imageUrl = '/' + response.url
      quill.insertEmbed(index, 'image', imageUrl)
      // 调整光标位置
      quill.setSelection(index + 1)
    }
  } else {
    ElMessage.error('图片插入失败')
  }
}

// 上传失败
const uploadError = () => {
  ElMessage.error('图片上传失败')
}

// 内容改变
const handleChange = (newContent: string) => {
  emit('update:modelValue', newContent)
}

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue !== content.value) {
      content.value = newValue
    }
  }
)
</script>

<style lang="scss" scoped>
.editor-container {
  :deep(.ql-container) {
    min-height: 400px;
  }

  :deep(.ql-editor) {
    min-height: 400px;
    line-height: normal;
  }
}

.avatar-uploader {
  display: none;
}
</style>

