<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getChapterApi, saveOrUpdateChapterApi, type NovelChapter } from '@/api/novel/novelApi'
import { loginUserStore } from '@/stores/loginUser'

const userStore = loginUserStore()
const visible = ref(false)
const isUpdate = ref(false)
const formRef = ref()
const submitting = ref(false)
const bookName = ref('')

const model = ref<NovelChapter>({
  bookId: 0,
  chapterOrder: 1,
  chapterTitle: '',
  chapterContent: ''
})

const emit = defineEmits<{
  submitSuccess: []
}>()

const rules = {
  chapterOrder: [{ required: true, message: '请输入章节号', trigger: 'blur' }],
  chapterTitle: [{ required: true, message: '请输入章节标题', trigger: 'blur' }],
  chapterContent: [{ required: true, message: '请输入章节内容', trigger: 'blur' }]
}

function beforeClose(done: () => void) {
  resetForm()
  done()
}

function resetForm() {
  submitting.value = false
  isUpdate.value = false
  bookName.value = ''
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
        bookName: bookName.value,
        authorAccount: userStore.account,
        authorName: userStore.authorName
      }
      await saveOrUpdateChapterApi(data)
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

defineExpose({
  show: async function (id?: number, novelId?: number, name?: string) {
    resetForm()
    visible.value = true
    bookName.value = name || ''

    if (id) {
      isUpdate.value = true
      submitting.value = true
      try {
        model.value = await getChapterApi(id)
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
    :title="isUpdate ? '编辑章节' : '新增章节'"
    width="700px"
    :before-close="beforeClose"
    draggable
    destroy-on-close
  >
    <el-form ref="formRef" v-loading="submitting" :model="model" :rules="rules" label-width="80px">
      <el-form-item label="所属小说">
        <el-input :model-value="bookName" disabled />
      </el-form-item>
      <el-form-item label="章节号" prop="chapterOrder">
        <el-input-number v-model="model.chapterOrder" :min="1" />
      </el-form-item>
      <el-form-item label="章节标题" prop="chapterTitle">
        <el-input v-model="model.chapterTitle" placeholder="请输入章节标题" />
      </el-form-item>
      <el-form-item label="章节内容" prop="chapterContent">
        <el-input
          v-model="model.chapterContent"
          type="textarea"
          :rows="10"
          placeholder="请输入章节内容"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>
