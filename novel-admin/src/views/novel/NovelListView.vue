<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import type { DataPage } from '@/api/commons/dataPage'
import { findNovelApi, deleteNovelApi, type Novel } from '@/api/novel/novelApi'
import { loginUserStore } from '@/stores/loginUser'
import NovelDialog from './NovelDialog.vue'

const router = useRouter()
const userStore = loginUserStore()
const loading = ref(false)
const novelDialog = ref()
const selected = ref<Novel[]>([])

const searchForm = ref({
  bookName: '',
  categoryName: ''
})

const novels = ref<DataPage<Novel>>({
  pageNumber: 1,
  pageSize: 10,
  totalCount: 0,
  content: []
})

async function findNovels() {
  loading.value = true
  try {
    novels.value = await findNovelApi(novels.value.pageNumber, novels.value.pageSize, {
      bookName: searchForm.value.bookName,
      categoryName: searchForm.value.categoryName,
      authorAccount: userStore.account // 只查询当前作者的书籍
    })
  } catch (error: any) {
    ElMessage.error(error.message || '获取数据失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  novels.value.pageNumber = 1
  findNovels()
}

function onReset() {
  searchForm.value = { bookName: '', categoryName: '' }
  onSearch()
}

function onSizeChange(size: number) {
  novels.value.pageSize = size
  findNovels()
}

function onCurrentChange(page: number) {
  novels.value.pageNumber = page
  findNovels()
}

function selectionChange(selections: Novel[]) {
  selected.value = selections
}

function showDialog(id?: number) {
  novelDialog.value.show(id)
}

function batchDelete() {
  const ids = selected.value.map((item) => item.id!)
  deleteNovels(ids)
}

function deleteNovels(ids: number[]) {
  ElMessageBox.confirm('确认删除选中的书籍吗？', '提示', {
    type: 'warning',
    draggable: true
  })
    .then(async () => {
      await deleteNovelApi(ids)
      ElMessage.success('删除成功')
      await findNovels()
    })
    .catch(() => {})
}

function goChapters(bookId: number, bookName: string) {
  router.push({ name: 'chapterList', query: { bookId, bookName } })
}

onMounted(() => {
  findNovels()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">我的书籍</span>
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

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="书籍名称">
        <el-input
          v-model="searchForm.bookName"
          placeholder="请输入书籍名称"
          clearable
          @keyup.enter="onSearch"
        />
      </el-form-item>
      <el-form-item label="书籍类型">
        <el-input
          v-model="searchForm.categoryName"
          placeholder="请输入类型"
          clearable
          @keyup.enter="onSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
        <el-button :icon="Refresh" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="novels.content"
      row-key="id"
      stripe
      border
      @selection-change="selectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="bookName" label="书籍名称" width="150" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="书籍类型" width="100" />
      <el-table-column prop="coverImage" label="封面" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.coverImage"
            :src="'/' + (row.coverImage.includes(',') ? row.coverImage.split(',')[0] : row.coverImage)"
            fit="cover"
            style="width: 60px; height: 80px"
          >
            <template #error>
              <div class="image-error">无图</div>
            </template>
          </el-image>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="authorName" label="作者姓名" width="100" />
      <el-table-column prop="price" label="价格" width="80">
        <template #default="{ row }">
          <el-tag v-if="!row.price || row.price === 0" type="success" size="small">免费</el-tag>
          <span v-else style="color: #e6a23c;">¥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="160" />
      <el-table-column prop="introduction" label="简介" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDialog(row.id)">编辑</el-button>
          <el-button link type="primary" @click="goChapters(row.id, row.bookName)">章节</el-button>
          <el-button link type="danger" @click="deleteNovels([row.id])">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="novels.pageNumber"
      v-model:page-size="novels.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="novels.totalCount"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
    />

    <NovelDialog ref="novelDialog" @submit-success="findNovels" />
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 16px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

.image-error {
  width: 60px;
  height: 80px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}
</style>
