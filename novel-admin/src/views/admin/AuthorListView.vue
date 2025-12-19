<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, View, Check } from '@element-plus/icons-vue'
import { getAuthorPage, deleteAuthors, auditAuthor, type Author } from '@/api/admin/authorApi'
import AuthorDialog from './AuthorDialog.vue'

const loading = ref(false)
const tableData = ref<Author[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = ref({ account: '', authorName: '', auditStatus: '' })
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view'>('add')
const currentRow = ref<Author | null>(null)

// 审核弹窗
const auditDialogVisible = ref(false)
const auditForm = ref({ id: 0, auditStatus: '是', auditReply: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await getAuthorPage({
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
  searchForm.value = { account: '', authorName: '', auditStatus: '' }
  handleSearch()
}

function handleAdd() {
  currentRow.value = null
  dialogMode.value = 'add'
  dialogVisible.value = true
}

function handleEdit(row: Author) {
  currentRow.value = row
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

function handleView(row: Author) {
  currentRow.value = row
  dialogMode.value = 'view'
  dialogVisible.value = true
}

function handleAudit(row: Author) {
  auditForm.value = {
    id: row.ID,
    auditStatus: row.AUDIT_STATUS || '待审核',
    auditReply: row.AUDIT_REPLY || ''
  }
  auditDialogVisible.value = true
}

async function submitAudit() {
  const res = await auditAuthor(auditForm.value)
  if (res.data.code === 0) {
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.data.msg || '审核失败')
  }
}

async function handleDelete(ids: number[]) {
  await ElMessageBox.confirm('确定要删除选中的作者吗？', '提示', { type: 'warning' })
  const res = await deleteAuthors(ids)
  if (res.data.code === 0) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.data.msg || '删除失败')
  }
}

function handleSelectionChange(rows: Author[]) {
  selectedIds.value = rows.map((r) => r.ID)
}

function handleDialogSuccess() {
  dialogVisible.value = false
  loadData()
}

function getAuditTagType(status: string | undefined) {
  if (status === '是') return 'success'
  if (status === '否') return 'danger'
  return 'warning'
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="账号">
          <el-input v-model="searchForm.account" placeholder="请输入账号" clearable />
        </el-form-item>
        <el-form-item label="作者姓名">
          <el-input v-model="searchForm.authorName" placeholder="请输入作者姓名" clearable />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.auditStatus" placeholder="请选择" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="是" value="是" />
            <el-option label="否" value="否" />
          </el-select>
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
          <span>作者列表</span>
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
        <el-table-column prop="ID" label="ID" width="80" />
        <el-table-column prop="ACCOUNT" label="账号" min-width="120" />
        <el-table-column prop="AUTHOR_NAME" label="作者姓名" min-width="120" />
        <el-table-column prop="GENDER" label="性别" width="80" />
        <el-table-column prop="AGE" label="年龄" width="80" />
        <el-table-column prop="PHONE" label="手机" min-width="120" />
        <el-table-column prop="EMAIL" label="邮箱" min-width="150" />
        <el-table-column prop="AUDIT_STATUS" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditTagType(row.AUDIT_STATUS)" size="small">
              {{ row.AUDIT_STATUS || '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link :icon="Check" @click="handleAudit(row)">审核</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete([row.ID])">
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

    <!-- 编辑弹窗 -->
    <AuthorDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :data="currentRow"
      @success="handleDialogSuccess"
    />

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="审核作者" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核状态">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio value="是">通过</el-radio>
            <el-radio value="否">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核回复">
          <el-input v-model="auditForm.auditReply" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
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
