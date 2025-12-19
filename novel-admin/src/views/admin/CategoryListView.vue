<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import {
  getCategoryPage,
  deleteCategories,
  addCategory,
  updateCategory,
  type Category
} from '@/api/admin/categoryApi'

const loading = ref(false)
const tableData = ref<Category[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = ref({ categoryName: '' })
const selectedIds = ref<number[]>([])

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const dialogForm = ref({ id: 0, categoryName: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await getCategoryPage({
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

function handleSearch() {
  currentPage.value = 1
  loadData()
}

function handleReset() {
  searchForm.value = { categoryName: '' }
  handleSearch()
}

function handleAdd() {
  dialogForm.value = { id: 0, categoryName: '' }
  dialogMode.value = 'add'
  dialogVisible.value = true
}

function handleEdit(row: Category) {
  dialogForm.value = { id: row.id, categoryName: row.CATEGORY_NAME }
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!dialogForm.value.categoryName) {
    ElMessage.warning('请输入类型名称')
    return
  }
  if (dialogMode.value === 'add') {
    const res = await addCategory({ categoryName: dialogForm.value.categoryName })
    if (res.data.code === 0) {
      ElMessage.success('新增成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.msg || '新增失败')
    }
  } else {
    const res = await updateCategory(dialogForm.value)
    if (res.data.code === 0) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  }
}

async function handleDelete(ids: number[]) {
  await ElMessageBox.confirm('确定要删除选中的类型吗？', '提示', { type: 'warning' })
  const res = await deleteCategories(ids)
  if (res.data.code === 0) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.data.msg || '删除失败')
  }
}

function handleSelectionChange(rows: Category[]) {
  selectedIds.value = rows.map((r) => r.id)
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="类型名称">
          <el-input v-model="searchForm.categoryName" placeholder="请输入类型名称" clearable />
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
          <span>小说类型列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
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
        <el-table-column prop="CATEGORY_NAME" label="类型名称" min-width="200" />
        <el-table-column prop="ADD_TIME" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
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
      :title="dialogMode === 'add' ? '新增类型' : '编辑类型'"
      width="400px"
    >
      <el-form :model="dialogForm" label-width="80px">
        <el-form-item label="类型名称" required>
          <el-input v-model="dialogForm.categoryName" placeholder="请输入类型名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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
