<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import {
  getCategoryPage,
  deleteCategories,
  addCategory,
  updateCategory,
  type Category
} from '@/api/admin/categoryApi'
import { useTable } from '@/composables'

// 使用通用表格逻辑
const {
  loading,
  tableData,
  total,
  currentPage,
  pageSize,
  selectedIds,
  searchForm,
  loadData,
  handleSearch,
  handleReset,
  handleSelectionChange,
  handleDelete,
  handlePageChange
} = useTable<Category, { categoryName: string }>({
  fetchApi: getCategoryPage,
  deleteApi: deleteCategories,
  searchParams: { categoryName: '' },
  deleteConfirmText: '确定要删除选中的类型吗？'
})

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const dialogForm = ref({ id: 0, categoryName: '' })

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
  
  try {
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
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
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
          <span>书籍类型列表</span>
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
            <el-button type="danger" link :icon="Delete" @click="handleDelete([row.id])">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handlePageChange"
        @current-change="handlePageChange"
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
