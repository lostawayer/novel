<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getNovelApi, saveOrUpdateNovelApi, type Novel } from '@/api/novel/novelApi'
import { loginUserStore } from '@/stores/loginUser'
import { Plus } from '@element-plus/icons-vue'

const userStore = loginUserStore()
const visible = ref(false)
const isUpdate = ref(false)
const formRef = ref()
const submitting = ref(false)

const model = ref<Novel>({
  bookName: '',
  categoryName: '',
  coverImage: '',
  introduction: '',
  authorAccount: '',
  authorName: '',
  price: 0
})

const emit = defineEmits<{
  submitSuccess: []
}>()

const rules = {
  bookName: [{ required: true, message: '请输入书籍名称', trigger: 'blur' }],
  categoryName: [{ required: true, message: '请输入书籍类型', trigger: 'blur' }]
}

function beforeClose(done: () => void) {
  resetForm()
  done()
}

function resetForm() {
  submitting.value = false
  isUpdate.value = false
  model.value = {
    bookName: '',
    categoryName: '',
    coverImage: '',
    introduction: '',
    authorAccount: userStore.account,
    authorName: userStore.authorName,
    price: 0
  }
}

async function submitForm() {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitting.value = true
    try {
      await saveOrUpdateNovelApi(model.value)
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
  show: async function (id?: number) {
    resetForm()
    visible.value = true

    if (id) {
      isUpdate.value = true
      submitting.value = true
      try {
        model.value = await getNovelApi(id)
      } catch (error: any) {
        ElMessage.error(error.message || '获取数据失败')
      } finally {
        submitting.value = false
      }
    }
  }
})
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="isUpdate ? '编辑书籍' : '新增书籍'"
    width="600px"
    :before-close="beforeClose"
    draggable
    destroy-on-close
  >
    <el-form ref="formRef" v-loading="submitting" :model="model" :rules="rules" label-width="80px">
      <el-form-item label="书籍名称" prop="bookName">
        <el-input v-model="model.bookName" placeholder="请输入书籍名称" />
      </el-form-item>
      <el-form-item label="书籍类型" prop="categoryName">
        <el-input v-model="model.categoryName" placeholder="请输入书籍类型，如：玄幻、都市" />
      </el-form-item>
      <el-form-item label="封面图片" prop="coverImage">
        <el-input v-model="model.coverImage" placeholder="请输入封面图片URL" />
      </el-form-item>
      <el-form-item label="作者账号">
        <el-input :model-value="model.authorAccount" disabled />
      </el-form-item>
      <el-form-item label="作者姓名">
        <el-input :model-value="model.authorName" disabled />
      </el-form-item>
      <el-form-item label="书籍简介" prop="introduction">
        <el-input
          v-model="model.introduction"
          type="textarea"
          :rows="4"
          placeholder="请输入书籍简介"
        />
      </el-form-item>
      <el-form-item label="书籍价格" prop="price">
        <el-input-number 
          v-model="model.price" 
          :min="0" 
          :precision="2" 
          :step="1"
          placeholder="0表示免费"
        />
        <span style="margin-left: 10px; color: #909399; font-size: 12px;">0表示免费，设置价格后非VIP用户需购买才能阅读VIP章节</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>
