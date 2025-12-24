<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, View, Back, Document, Clock } from '@element-plus/icons-vue'
import {
  getChapterPage,
  deleteChapters,
  addChapter,
  updateChapter,
  getChapterById,
  type Chapter
} from '@/api/admin/chapterApi'

const route = useRoute()
const router = useRouter()
const bookId = computed(() => Number(route.query.bookId) || 0)
const bookName = computed(() => (route.query.bookName as string) || '')

const loading = ref(false)
const tableData = ref<Chapter[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = ref({ chapterTitle: '' })
const selectedIds = ref<number[]>([])

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view'>('add')
const dialogLoading = ref(false)
const lastSaveTime = ref('')
const dialogForm = ref({
  id: 0,
  refId: 0,
  chapterNum: 1,
  chapterTitle: '',
  content: '',
  vipRead: '否'
})

// 字数统计
const wordCount = computed(() => {
  const content = dialogForm.value.content || ''
  return content.replace(/<[^>]+>/g, '').replace(/\s/g, '').length
})

// HTML 转纯文本（编辑时显示）
function htmlToText(html: string | undefined): string {
  if (!html) return ''
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
  if (text.includes('<p>')) return text
  const paragraphs = text.split(/\n+/).filter((p) => p.trim())
  return paragraphs.map((p) => `<p>${p.trim()}</p>`).join('\n')
}

async function loadData() {
  if (!bookId.value) return
  loading.value = true
  try {
    const res = await getChapterPage({
      page: currentPage.value,
      limit: pageSize.value,
      refId: bookId.value,
      sort: 'CHAPTER_NUM',
      order: 'asc',
      ...searchForm.value
    })
    if (res.data.code === 0) {
      tableData.value = res.data.data.list
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadData()
}

function handleReset() {
  searchForm.value = { chapterTitle: '' }
  handleSearch()
}

function handleBack() {
  router.back()
}

function handleAdd() {
  const maxNum = tableData.value.reduce((max, c) => Math.max(max, c.CHAPTER_NUM), 0)
  dialogForm.value = {
    id: 0,
    refId: bookId.value,
    chapterNum: maxNum + 1,
    chapterTitle: '',
    content: '',
    vipRead: '否'
  }
  lastSaveTime.value = ''
  dialogMode.value = 'add'
  dialogVisible.value = true
}

async function handleEdit(row: Chapter) {
  dialogLoading.value = true
  dialogVisible.value = true
  dialogMode.value = 'edit'
  lastSaveTime.value = ''
  try {
    const res = await getChapterById(row.id)
    if (res.data.code === 0) {
      const data = res.data.data
      dialogForm.value = {
        id: data.id,
        refId: data.REF_ID,
        chapterNum: data.CHAPTER_NUM,
        chapterTitle: data.CHAPTER_TITLE,
        content: htmlToText(data.CONTENT) || '',
        vipRead: data.VIP_READ || '否'
      }
    }
  } finally {
    dialogLoading.value = false
  }
}

async function handleView(row: Chapter) {
  dialogLoading.value = true
  dialogVisible.value = true
  dialogMode.value = 'view'
  lastSaveTime.value = ''
  try {
    const res = await getChapterById(row.id)
    if (res.data.code === 0) {
      const data = res.data.data
      dialogForm.value = {
        id: data.id,
        refId: data.REF_ID,
        chapterNum: data.CHAPTER_NUM,
        chapterTitle: data.CHAPTER_TITLE,
        content: htmlToText(data.CONTENT) || '',
        vipRead: data.VIP_READ || '否'
      }
    }
  } finally {
    dialogLoading.value = false
  }
}

async function handleSubmit() {
  if (!dialogForm.value.chapterTitle) {
    ElMessage.warning('请输入章节标题')
    return
  }
  const submitData = {
    ...dialogForm.value,
    content: textToHtml(dialogForm.value.content)
  }
  if (dialogMode.value === 'add') {
    const res = await addChapter(submitData)
    if (res.data.code === 0) {
      ElMessage.success('新增成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.msg || '新增失败')
    }
  } else if (dialogMode.value === 'edit') {
    const res = await updateChapter(submitData)
    if (res.data.code === 0) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  } else {
    dialogVisible.value = false
  }
}

// 保存草稿
async function saveDraft() {
  if (!dialogForm.value.chapterTitle) {
    ElMessage.warning('请输入章节标题')
    return
  }
  const submitData = {
    ...dialogForm.value,
    content: textToHtml(dialogForm.value.content)
  }
  if (dialogMode.value === 'add') {
    const res = await addChapter(submitData)
    if (res.data.code === 0) {
      lastSaveTime.value = new Date().toLocaleTimeString()
      ElMessage.success('草稿已保存')
      // 保存后切换为编辑模式
      dialogMode.value = 'edit'
      loadData()
    } else {
      ElMessage.error(res.data.msg || '保存失败')
    }
  } else {
    const res = await updateChapter(submitData)
    if (res.data.code === 0) {
      lastSaveTime.value = new Date().toLocaleTimeString()
      ElMessage.success('草稿已保存')
    } else {
      ElMessage.error(res.data.msg || '保存失败')
    }
  }
}

async function handleDelete(ids: number[]) {
  await ElMessageBox.confirm('确定要删除选中的章节吗？', '提示', { type: 'warning' })
  const res = await deleteChapters(ids)
  if (res.data.code === 0) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.data.msg || '删除失败')
  }
}

function handleSelectionChange(rows: Chapter[]) {
  selectedIds.value = rows.map((r) => r.id)
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <div class="search-header">
        <el-button :icon="Back" @click="handleBack">返回</el-button>
        <span class="book-title">《{{ bookName }}》章节管理</span>
      </div>
      <el-form :model="searchForm" inline style="margin-top: 16px">
        <el-form-item label="章节标题">
          <el-input v-model="searchForm.chapterTitle" placeholder="请输入章节标题" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>章节列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增章节</el-button>
            <el-button
              type="danger"
              :icon="Delete"
              :disabled="!selectedIds.length"
              @click="handleDelete(selectedIds)"
            >
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="CHAPTER_NUM" label="章节号" width="100" />
        <el-table-column prop="CHAPTER_TITLE" label="章节标题" min-width="200" />
        <el-table-column prop="VIP_READ" label="VIP阅读" width="100">
          <template #default="{ row }">
            <el-tag :type="row.VIP_READ === '是' ? 'warning' : 'info'" size="small">
              {{ row.VIP_READ || '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ADD_TIME" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete([row.id])">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增章节' : dialogMode === 'edit' ? '编辑章节' : '查看章节'"
      width="900px"
      top="5vh"
      :close-on-click-modal="false"
      :lock-scroll="true"
      :append-to-body="true"
      destroy-on-close
    >
      <div class="editor-container">
        <!-- 顶部信息栏 -->
        <div class="editor-header">
          <div class="book-info">
            <el-icon><Document /></el-icon>
            <span>《{{ bookName }}》</span>
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

        <el-form v-loading="dialogLoading" :model="dialogForm" label-position="top" :disabled="dialogMode === 'view'">
          <!-- 章节信息行 -->
          <div class="chapter-info-row">
            <el-form-item label="章节号" class="chapter-order">
              <el-input-number v-model="dialogForm.chapterNum" :min="1" :max="9999" controls-position="right" />
            </el-form-item>
            <el-form-item label="章节标题" class="chapter-title" required>
              <el-input v-model="dialogForm.chapterTitle" placeholder="请输入章节标题" maxlength="100" show-word-limit />
            </el-form-item>
            <el-form-item label="VIP阅读" class="vip-switch">
              <el-switch v-model="dialogForm.vipRead" active-value="是" inactive-value="否" active-text="是" inactive-text="否" />
            </el-form-item>
          </div>

          <!-- 内容编辑区 -->
          <el-form-item label="章节内容" class="content-area">
            <el-input
              v-model="dialogForm.content"
              type="textarea"
              :rows="12"
              placeholder="在这里编辑章节内容...

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
            <el-button v-if="dialogMode !== 'view'" @click="saveDraft" :loading="dialogLoading">
              <el-icon><Document /></el-icon>
              保存草稿
            </el-button>
          </div>
          <div class="footer-right">
            <el-button @click="dialogVisible = false">{{ dialogMode === 'view' ? '关闭' : '取消' }}</el-button>
            <el-button v-if="dialogMode !== 'view'" type="primary" @click="handleSubmit">
              {{ dialogMode === 'add' ? '发布章节' : '保存修改' }}
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  padding: 0;
}
.search-card {
  margin-bottom: 16px;
}
.search-header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.book-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.table-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

/* 章节编辑弹窗样式 */
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
  width: 120px;
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
