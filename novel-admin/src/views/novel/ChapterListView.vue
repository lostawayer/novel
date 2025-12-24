<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
import type { DataPage } from '@/api/commons/dataPage'
import { findChapterApi, deleteChapterApi, type NovelChapter } from '@/api/novel/novelApi'
import ChapterDialog from './ChapterDialog.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const chapterDialog = ref()
const selected = ref<NovelChapter[]>([])
const bookId = ref<number>(0)
const bookName = ref<string>('')

const chapters = ref<DataPage<NovelChapter>>({
  pageNumber: 1,
  pageSize: 10,
  totalCount: 0,
  content: []
})

// 去除 HTML 标签，只显示纯文本预览
function stripHtml(html: string | undefined): string {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').substring(0, 100)
}

async function findChapters() {
  loading.value = true
  try {
    chapters.value = await findChapterApi(
      chapters.value.pageNumber,
      chapters.value.pageSize,
      bookId.value
    )
  } catch (error: any) {
    ElMessage.error(error.message || '获取数据失败')
  } finally {
    loading.value = false
  }
}

function onSizeChange(size: number) {
  chapters.value.pageSize = size
  findChapters()
}

function onCurrentChange(page: number) {
  chapters.value.pageNumber = page
  findChapters()
}

function selectionChange(selections: NovelChapter[]) {
  selected.value = selections
}

function showDialog(id?: number) {
  chapterDialog.value.show(id, bookId.value, bookName.value)
}

function batchDelete() {
  const ids = selected.value.map((item) => item.id!)
  deleteChapters(ids)
}

function deleteChapters(ids: number[]) {
  ElMessageBox.confirm('确认删除选中的章节吗？', '提示', {
    type: 'warning',
    draggable: true
  })
    .then(async () => {
      await deleteChapterApi(ids)
      ElMessage.success('删除成功')
      await findChapters()
    })
    .catch(() => {})
}

function goBack() {
  router.push({ name: 'novelList' })
}

onMounted(() => {
  bookId.value = Number(route.query.bookId) || 0
  bookName.value = (route.query.bookName as string) || ''
  findChapters()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <div class="title-area">
          <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
          <span class="title">章节管理 - {{ bookName }}</span>
        </div>
        <div class="actions">
          <el-button type="primary" :icon="Plus" @click="showDialog()">新增</el-button>
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="selected.length === 0"
            @click="batchDelete"
            >删除</el-button
          >
        </div>
      </div>
    </template>

    <el-table
      v-loading="loading"
      :data="chapters.content"
      row-key="id"
      stripe
      border
      @selection-change="selectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="chapterOrder" label="章节号" width="100" />
      <el-table-column prop="chapterTitle" label="章节标题" width="200" show-overflow-tooltip />
      <el-table-column label="内容预览" min-width="300" show-overflow-tooltip>
        <template #default="{ row }">
          {{ stripHtml(row.chapterContent) }}
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDialog(row.id)">编辑</el-button>
          <el-button link type="danger" @click="deleteChapters([row.id])">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="chapters.pageNumber"
      v-model:page-size="chapters.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="chapters.totalCount"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
    />

    <ChapterDialog ref="chapterDialog" @submit-success="findChapters" />
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
