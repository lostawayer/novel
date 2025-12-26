<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Edit, View, Document } from '@element-plus/icons-vue'
import { getBookPage, deleteBooks, getBookById, updateBook, type NovelInfo } from '@/api/admin/bookApi'
import { getCategoryOptions } from '@/api/admin/optionApi'

const router = useRouter()
const loading = ref(false)
const tableData = ref<NovelInfo[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = ref({ novelName: '', categoryName: '', authorName: '' })
const selectedIds = ref<number[]>([])
const categoryOptions = ref<{ label: string; value: string }[]>([])

// 详情弹窗
const detailVisible = ref(false)
const currentRow = ref<NovelInfo | null>(null)

// 编辑弹窗
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = ref({
  id: 0,
  novelName: '',
  categoryName: '',
  picture: '',
  description: '',
  price: 0
})

async function loadData() {
  loading.value = true
  try {
    const res = await getBookPage({
      page: currentPage.value,
      limit: pageSize.value,
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

async function loadOptions() {
  try {
    const res = await getCategoryOptions()
    if (res.data.code === 0) {
      categoryOptions.value = res.data.data
    }
  } catch (e) {}
}

function handleSearch() {
  currentPage.value = 1
  loadData()
}

function handleReset() {
  searchForm.value = { novelName: '', categoryName: '', authorName: '' }
  handleSearch()
}

function handleView(row: NovelInfo) {
  currentRow.value = row
  detailVisible.value = true
}

async function handleEdit(row: NovelInfo) {
  editLoading.value = true
  editVisible.value = true
  try {
    const res = await getBookById(row.id)
    if (res.data.code === 0) {
      const data = res.data.data as any
      editForm.value = {
        id: data.id,
        novelName: data.NOVEL_NAME || data.novelName || '',
        categoryName: data.CATEGORY_NAME || data.categoryName || '',
        picture: data.PICTURE || data.picture || '',
        description: data.DESCRIPTION || data.description || '',
        price: data.PRICE || data.price || 0
      }
    } else {
      ElMessage.error(res.data.msg || '获取书籍信息失败')
      editVisible.value = false
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取书籍信息失败')
    editVisible.value = false
  } finally {
    editLoading.value = false
  }
}

async function submitEdit() {
  if (!editForm.value.novelName) {
    ElMessage.warning('请输入书籍名称')
    return
  }
  const res = await updateBook(editForm.value)
  if (res.data.code === 0) {
    ElMessage.success('更新成功')
    editVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.data.msg || '更新失败')
  }
}

function handleChapter(row: NovelInfo) {
  router.push({ name: 'chapterManage', query: { bookId: row.id, bookName: row.NOVEL_NAME } })
}

async function handleDelete(ids: number[]) {
  await ElMessageBox.confirm('确定要删除选中的书籍吗？相关章节和评论也会被删除！', '提示', {
    type: 'warning'
  })
  const res = await deleteBooks(ids)
  if (res.data.code === 0) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.data.msg || '删除失败')
  }
}

function handleSelectionChange(rows: NovelInfo[]) {
  selectedIds.value = rows.map((r) => r.id)
}

onMounted(() => {
  loadData()
  loadOptions()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="书籍名称">
          <el-input v-model="searchForm.novelName" placeholder="请输入书籍名称" clearable />
        </el-form-item>
        <el-form-item label="书籍类型">
          <el-input v-model="searchForm.categoryName" placeholder="请输入类型" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.authorName" placeholder="请输入作者" clearable />
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
          <span>书籍列表</span>
          <div class="header-actions">
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="NOVEL_NAME" label="书籍名称" min-width="150" />
        <el-table-column prop="CATEGORY_NAME" label="类型" width="100" />
        <el-table-column prop="AUTHOR_NAME" label="作者" width="120" />
        <el-table-column label="价格" width="80">
          <template #default="{ row }">
            <el-tag v-if="!row.PRICE || row.PRICE === 0" type="success" size="small">免费</el-tag>
            <span v-else style="color: #e6a23c;">¥{{ row.PRICE }}</span>
          </template>
        </el-table-column>
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.PICTURE"
              :src="'/' + row.PICTURE.split(',')[0]"
              style="width: 50px; height: 60px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="PUBLISH_TIME" label="发布时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link :icon="Document" @click="handleChapter(row)">
              章节
            </el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="书籍详情" width="600px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="书籍名称">{{ currentRow.NOVEL_NAME }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentRow.CATEGORY_NAME }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentRow.AUTHOR_NAME }}</el-descriptions-item>
        <el-descriptions-item label="账号">{{ currentRow.ACCOUNT }}</el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{
          currentRow.PUBLISH_TIME
        }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ currentRow.DESCRIPTION }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑书籍" width="600px">
      <el-form v-loading="editLoading" :model="editForm" label-width="80px">
        <el-form-item label="书籍名称" required>
          <el-input v-model="editForm.novelName" placeholder="请输入书籍名称" />
        </el-form-item>
        <el-form-item label="书籍类型">
          <el-select v-model="editForm.categoryName" placeholder="请选择类型" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="editForm.picture" placeholder="图片路径" />
        </el-form-item>
        <el-form-item label="书籍简介">
          <el-input v-model="editForm.description" type="textarea" :rows="4" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="书籍价格">
          <el-input-number v-model="editForm.price" :min="0" :precision="2" :step="1" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">0表示免费</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
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
</style>
