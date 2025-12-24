<template>
  <div>
    <!-- 上传文件组件 -->
    <el-upload
      ref="uploadRef"
      :action="actionUrl"
      list-type="picture-card"
      :multiple="multiple"
      :limit="limit"
      :headers="headers"
      :file-list="fileList"
      :on-exceed="handleExceed"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="handleBeforeUpload"
    >
      <el-icon><Plus /></el-icon>
      <template #tip>
        <div class="el-upload__tip" style="color: #838fa1">{{ tip }}</div>
      </template>
    </el-upload>

    <el-dialog v-model="dialogVisible" title="查看图片" append-to-body>
      <img :src="dialogImageUrl" alt="preview" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { UploadFile, UploadFiles, UploadProps, UploadUserFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getToken } from '@/common/storage'
import config from '@/config/config'

interface Props {
  tip?: string
  action?: string
  limit?: number
  multiple?: boolean
  fileUrls?: string
}

const props = withDefaults(defineProps<Props>(), {
  tip: '点击上传图片',
  action: '/file/upload',
  limit: 3,
  multiple: true,
  fileUrls: '',
})

const emit = defineEmits<{
  change: [value: string]
}>()

const uploadRef = ref()
const dialogVisible = ref(false)
const dialogImageUrl = ref('')
const fileList = ref<UploadUserFile[]>([])
const fileUrlList = ref<string[]>([])

const headers = computed(() => ({
  Token: getToken() || '',
}))

const actionUrl = computed(() => {
  return config.baseUrl + props.action
})

// 初始化文件列表
const init = () => {
  if (props.fileUrls) {
    fileUrlList.value = props.fileUrls.split(',').filter((url) => url)
    const fileArray: UploadUserFile[] = fileUrlList.value.map((url, index) => ({
      name: `${index}`,
      url: url.startsWith('http') ? url : config.baseUrl + '/' + url,
    }))
    fileList.value = fileArray
  } else {
    fileList.value = []
    fileUrlList.value = []
  }
}

// 上传前钩子
const handleBeforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  // 可以添加文件类型和大小验证
  return true
}

// 上传成功
const handleSuccess: UploadProps['onSuccess'] = (response, uploadFile, uploadFiles) => {
  if (response && response.code === 0) {
    const url = 'upload/' + response.file
    // 设置上传文件的url，供setFileList使用
    uploadFile.url = url
    // 更新文件列表
    setFileList(uploadFiles)
    emit('change', fileUrlList.value.join(','))
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传失败
const handleError: UploadProps['onError'] = (error) => {
  ElMessage.error('文件上传失败')
}

// 移除文件
const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  setFileList(uploadFiles)
  emit('change', fileUrlList.value.join(','))
}

// 预览图片
const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url || ''
  dialogVisible.value = true
}

// 超出限制
const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
  ElMessage.warning(`最多上传 ${props.limit} 张图片`)
}

// 设置文件列表
const setFileList = (uploadFiles: UploadFiles) => {
  const fileArray: UploadUserFile[] = []
  const fileUrlArray: string[] = []
  const token = getToken()

  uploadFiles.forEach((item, index) => {
    let url = item.url || ''
    // 去掉查询参数
    url = url.split('?')[0]
    // 如果是blob地址，跳过（上传中的临时文件）
    if (url.startsWith('blob:')) {
      return
    }
    
    // 提取相对路径用于保存到数据库
    let relativePath = url
    if (url.startsWith('http')) {
      // 从完整URL中提取 upload/xxx.jpg 部分
      const match = url.match(/upload\/[^?]+/)
      relativePath = match ? match[0] : url
    }
    
    // 构建完整URL用于显示
    let displayUrl = url
    if (url && !url.startsWith('http')) {
      displayUrl = config.baseUrl + '/' + url
    }
    
    fileArray.push({
      name: item.name,
      url: displayUrl + '?token=' + token,
    })
    // 存储相对路径用于保存到数据库
    fileUrlArray.push(relativePath)
  })

  fileList.value = fileArray
  fileUrlList.value = fileUrlArray
}

watch(
  () => props.fileUrls,
  () => {
    init()
  }
)

onMounted(() => {
  init()
})
</script>

<style lang="scss" scoped>
:deep(.el-upload--picture-card) {
  width: 148px;
  height: 148px;
  line-height: 148px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 148px;
  height: 148px;
}
</style>

