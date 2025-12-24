<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getChapterApi, saveOrUpdateChapterApi, type NovelChapter } from '@/api/novel/novelApi'
import { loginUserStore } from '@/stores/loginUser'
import { Edit, Document, Clock, Promotion } from '@element-plus/icons-vue'

const userStore = loginUserStore()
const visible = ref(false)
const isUpdate = ref(false)
const formRef = ref()
const submitting = ref(false)
const bookName = ref('')
const lastSaveTime = ref('')
const autoSaveTimer = ref<number | null>(null)

const model = ref<NovelChapter>({
  bookId: 0,
  chapterOrder: 1,
  chapterTitle: '',
  chapterContent: '',
  vipOnly: '否'
})

const emit = defineEmits<{
  submitSuccess: []
}>()

const rules = {
  chapterOrder: [{ required: true, message: '请输入章节号', trigger: 'blur' }],
  chapterTitle: [{ required: true, message: '请输入章节标题', trigger: 'blur' }]
}

// 字数统计
const wordCount = computed(() => {
  const content = model.value.chapterContent || ''
  return content.replace(/<[^>]+>/g, '').replace(/\s/g, '').length
})

function beforeClose(done: () => void) {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
  }
  resetForm()
  done()
}

function resetForm() {
  submitting.value = false
  isUpdate.value = false
  bookName.value = ''
  lastSaveTime.value = ''
  model.value = {
    bookId: 0,
    chapterOrder: 1,
    chapterTitle: '',
    chapterContent: ''
  }
}

async function submitForm() {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        ...model.value,
        chapterContent: textToHtml(model.value.chapterContent),
        bookName: bookName.value,
        authorAccount: userStore.account,
        authorName: userStore.authorName
      }
      await saveOrUpdateChapterApi(data)
      lastSaveTime.value = new Date().toLocaleTimeString()
      ElMessage.success(isUpdate.value ? '修改成功' : '新增成功')
      visible.value = false
      emit('submitSuccess')
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// 保存草稿
async function saveDraft() {
  if (!model.value.chapterTitle || !model.value.chapterContent) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  submitting.value = true
  try {
    const data = {
      ...model.value,
      chapterContent: textToHtml(model.value.chapterContent),
      bookName: bookName.value,
      authorAccount: userStore.account,
      authorName: userStore.authorName
    }
    await saveOrUpdateChapterApi(data)
    lastSaveTime.value = new Date().toLocaleTimeString()
    ElMessage.success('草稿已保存')
    if (!isUpdate.value && model.value.id) {
      isUpdate.value = true
    }
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

// HTML 转纯文本（编辑时显示）
function htmlToText(html: string | undefined): string {
  if (!html) return ''
  // 将 </p><p> 或 </p>\n<p> 转为换行
  return html
    .replace(/<\/p>\s*<p>/gi, '\n\n')
    .replace(/<p>/gi, '')
    .replace(/<\/p>/gi, '')
    .replace(/<br\s*\/?>/gi, '\n')
    .trim()
}

// 纯文本转 HTML（保存时）
function textToHtml(text: string | undefined): string {
  if (!text) return ''
  // 如果已经包含 <p> 标签，直接返回
  if (text.includes('<p>')) return text
  // 按换行分段
  const paragraphs = text.split(/\n+/).filter((p) => p.trim())
  return paragraphs.map((p) => `<p>${p.trim()}</p>`).join('\n')
}

defineExpose({
  show: async function (id?: number, novelId?: number, name?: string) {
    resetForm()
    visible.value = true
    bookName.value = name || ''

    if (id) {
      isUpdate.value = true
      submitting.value = true
      try {
        const data = await getChapterApi(id)
        // 将 HTML 内容转为纯文本显示
        data.chapterContent = htmlToText(data.chapterContent)
        model.value = data
      } catch (error: any) {
        ElMessage.error(error.message || '获取数据失败')
      } finally {
        submitting.value = false
      }
    } else if (novelId) {
      model.value.bookId = novelId
    }
  }
})
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="isUpdate ? '编辑章节' : '创作新章节'"
    width="900px"
    top="5vh"
    :before-close="beforeClose"
    destroy-on-close
    class="chapter-dialog"
    :fullscreen="false"
    :lock-scroll="true"
    :close-on-click-modal="false"
    append-to-body
  >
    <div class="editor-container">
      <!-- 顶部信息栏 -->
      <div class="editor-header">
        <div class="book-info">
          <el-icon><Document /></el-icon>
          <span>{{ bookName }}</span>
        </div>
        <div class="editor-stats">
          <span class="word-count">
            <el-icon><Edit /></el-icon>
            字数：{{ wordCount }}
          </span>
          <span v-if="lastSaveTime" class="save-time">
            <el-icon><Clock /></el-icon>
            上次保存：{{ lastSaveTime }}
          </span>
        </div>
      </div>

      <el-form ref="formRef" v-loading="submitting" :model="model" :rules="rules" label-position="top">
        <!-- 章节信息行 -->
        <div class="chapter-info-row">
          <el-form-item label="章节号" prop="chapterOrder" class="chapter-order">
            <el-input-number v-model="model.chapterOrder" :min="1" :max="9999" controls-position="right" />
          </el-form-item>
          <el-form-item label="章节标题" prop="chapterTitle" class="chapter-title">
            <el-input 
              v-model="model.chapterTitle" 
              placeholder="请输入一个吸引人的章节标题" 
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="VIP章节" class="vip-switch">
            <el-switch 
              v-model="model.vipOnly" 
              active-value="是" 
              inactive-value="否"
              active-text="是"
              inactive-text="否"
            />
          </el-form-item>
        </div>

        <!-- 内容编辑区 -->
        <el-form-item label="章节内容" class="content-area">
          <el-input
            v-model="model.chapterContent"
            type="textarea"
            :rows="10"
            placeholder="在这里开始你的创作...

每个段落之间用空行分隔，保存时会自动格式化。"
            resize="none"
            class="content-textarea"
          />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <el-button @click="saveDraft" :loading="submitting">
            <el-icon><Document /></el-icon>
            保存草稿
          </el-button>
        </div>
        <div class="footer-right">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submitForm">
            <el-icon><Promotion /></el-icon>
            {{ isUpdate ? '保存修改' : '发布章节' }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.chapter-dialog :deep(.el-overlay) {
  overflow: hidden;
}

.chapter-dialog :deep(.el-dialog) {
  margin: 5vh auto !important;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.chapter-dialog :deep(.el-dialog__body) {
  padding: 0;
  flex: 1;
  overflow-y: auto;
}

.editor-container {
  padding: 20px;
  background: #fafafa;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.book-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.editor-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.word-count, .save-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.chapter-info-row {
  display: flex;
  gap: 16px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.chapter-order {
  width: 140px;
  flex-shrink: 0;
}

.chapter-title {
  flex: 1;
}

.vip-switch {
  width: 100px;
  flex-shrink: 0;
}

.content-area {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.content-textarea :deep(.el-textarea__inner) {
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  font-size: 15px;
  line-height: 1.8;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.content-textarea :deep(.el-textarea__inner:focus) {
  background: #fff;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.footer-left, .footer-right {
  display: flex;
  gap: 10px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}
</style>
