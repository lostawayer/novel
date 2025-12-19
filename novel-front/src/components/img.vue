<template>
  <el-dialog v-model="visible" title="拍照上传" width="1065px">
    <div class="box">
      <video id="videoCamera" ref="videoRef" class="canvas" :width="videoWidth" :height="videoHeight" autoplay></video>
      <canvas
        id="canvasCamera"
        ref="canvasRef"
        class="canvas"
        :width="videoWidth"
        :height="videoHeight"
      ></canvas>
    </div>
    <template #footer>
      <div style="display: flex">
        <el-upload
          :action="actionUrl"
          :on-success="uploadSuccess"
          :show-file-list="false"
          accept=".jpg,.png,.jpeg"
          style="margin-right: 10px"
        >
          <el-button icon="Upload" size="small">上传图片</el-button>
        </el-upload>

        <el-button @click="drawImage" icon="Camera" size="small">拍照</el-button>
        <el-button v-if="isCameraClosed" @click="getCompetence" icon="VideoCamera" size="small">
          打开摄像头
        </el-button>
        <el-button v-else @click="stopNavigator" icon="SwitchButton" size="small">
          关闭摄像头
        </el-button>
        <el-button @click="resetCanvas" icon="Refresh" size="small">重置</el-button>
        <el-button @click="onCancel" icon="CircleClose" size="small">完成</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { post } from '@/utils/request'
import config from '@/config/config'

const visible = ref(false)
const isCameraClosed = ref(false)
const videoRef = ref<HTMLVideoElement>()
const canvasRef = ref<HTMLCanvasElement>()
const videoWidth = 500
const videoHeight = 400
const imgSrc = ref('')

const emit = defineEmits<{
  imgChange: [file: string]
}>()

const actionUrl = computed(() => {
  return config.baseUrl + '/file/upload'
})

// 打开拍照对话框
const onTake = () => {
  visible.value = true
  getCompetence()
}

// 关闭对话框
const onCancel = () => {
  if (imgSrc.value) {
    uploadImg()
  }
  visible.value = false
  stopNavigator()
}

// 上传成功回调
const uploadSuccess = (response: any) => {
  visible.value = false
  emit('imgChange', response.file)
}

// 上传图片
const uploadImg = () => {
  if (!imgSrc.value) return

  const formData = new FormData()
  formData.append('file', base64toFile(imgSrc.value))

  post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }).then((res: any) => {
    emit('imgChange', res.file)
  })
}

// Base64 转 File
const base64toFile = (dataurl: string, filename: string = 'file') => {
  const arr = dataurl.split(',')
  const mime = arr[0].match(/:(.*?);/)?.[1] || 'image/png'
  const suffix = mime.split('/')[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new File([u8arr], `${filename}.${suffix}`, { type: mime })
}

// 获取摄像头权限
const getCompetence = () => {
  nextTick(() => {
    isCameraClosed.value = false

    const canvas = canvasRef.value
    const video = videoRef.value

    if (!canvas || !video) {
      ElMessage.error('无法获取视频或画布元素')
      return
    }

    // 检查浏览器兼容性
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      ElMessage.error('您的浏览器不支持摄像头功能')
      return
    }

    const constraints = {
      audio: false,
      video: {
        width: videoWidth,
        height: videoHeight,
      },
    }

    navigator.mediaDevices
      .getUserMedia(constraints)
      .then((stream) => {
        if ('srcObject' in video) {
          video.srcObject = stream
        } else {
          // 兼容旧浏览器
          ;(video as any).src = window.URL.createObjectURL(stream as any)
        }
        video.onloadedmetadata = () => {
          video.play()
        }
      })
      .catch((err) => {
        console.error('摄像头权限获取失败:', err)
        ElMessage.warning('没有开启摄像头权限或浏览器版本不兼容')
      })
  })
}

// 拍照
const drawImage = () => {
  const canvas = canvasRef.value
  const video = videoRef.value

  if (!canvas || !video) return

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.drawImage(video, 0, 0, videoWidth, videoHeight)
  imgSrc.value = canvas.toDataURL('image/png')
}

// 清空画布
const clearCanvas = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.clearRect(0, 0, canvas.width, canvas.height)
}

// 重置画布
const resetCanvas = () => {
  imgSrc.value = ''
  clearCanvas()
}

// 关闭摄像头
const stopNavigator = () => {
  const video = videoRef.value
  if (video && video.srcObject) {
    const stream = video.srcObject as MediaStream
    const tracks = stream.getTracks()
    tracks.forEach((track) => track.stop())
    video.srcObject = null
    isCameraClosed.value = true
  }
}

// 暴露方法供父组件调用
defineExpose({
  onTake,
})
</script>

<style lang="scss" scoped>
.box {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.canvas {
  border: 1px solid #ddd;
  border-radius: 4px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}
</style>

