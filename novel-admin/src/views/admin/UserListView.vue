<script setup lang="ts">
import { onMounted } from 'vue'
import { Search, Refresh, Plus, Delete, Edit, View } from '@element-plus/icons-vue'
import { getUserPage, deleteUsers, type Reader } from '@/api/admin/userApi'
import { useTable, useDialog } from '@/composables'
import UserDialog from './UserDialog.vue'

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
} = useTable<Reader, { username: string; realName: string }>({
  fetchApi: getUserPage,
  deleteApi: deleteUsers,
  searchParams: { username: '', realName: '' },
  deleteConfirmText: '确定要删除选中的用户吗？'
})

// 使用弹窗逻辑
const dialog = useDialog<Reader>()

function handleDialogSuccess() {
  dialog.close()
  loadData()
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable />
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
          <span>用户列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="dialog.openAdd()">新增</el-button>
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
        <el-table-column prop="USERNAME" label="用户名" min-width="120" />
        <el-table-column prop="NICKNAME" label="昵称" min-width="120" />
        <el-table-column prop="REAL_NAME" label="姓名" min-width="100" />
        <el-table-column prop="GENDER" label="性别" width="80" />
        <el-table-column prop="PHONE" label="手机" min-width="120" />
        <el-table-column prop="EMAIL" label="邮箱" min-width="150" />
        <el-table-column prop="VIP" label="VIP" width="80">
          <template #default="{ row }">
            <el-tag :type="row.VIP === '是' ? 'success' : 'info'" size="small">
              {{ row.VIP || '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="dialog.openView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="dialog.openEdit(row)">编辑</el-button>
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
    <UserDialog
      v-model:visible="dialog.visible.value"
      :mode="dialog.mode.value"
      :data="dialog.currentRow.value"
      @success="handleDialogSuccess"
    />
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
