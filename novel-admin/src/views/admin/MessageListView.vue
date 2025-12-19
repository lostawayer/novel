<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, View, ChatLineRound } from '@element-plus/icons-vue'
import { getMessagePage, deleteMessages, replyMessage, type Message } from '@/api/admin/messageApi'

const loading = ref(false)
const tableData = ref<Message[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = ref({ username: '' })
const selectedIds = ref<number[]>([])

// 详情弹窗
const detailVisible = ref(false)
const currentRow = ref<Message | null>(null)

// 回复弹窗
const replyVisible = ref(false)
const replyForm = ref({ id: 0, reply: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await getMessagePage({
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
  searchForm.value = { username: '' }
  handleSearch()
}

function handleView(row: Message) {
  currentRow.value = row
  detailVisible.value = true
}

function handleReply(row: Message) {
  replyForm.value = { id: row.id, reply: row.REPLY || '' }
  replyVisible.value = true
}

async function submitReply() {
  const res = await replyMessage(replyForm.value)
  if (res.data.code === 0) {
    ElMessage.success('回复成功')
    replyVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.data.msg || '回复失败')
  }
}

async function handleDelete(ids: number[]) {
  await ElMessageBox.confirm('确定要删除选中的留言吗？', '提示', { type: 'warning' })
  const res = await deleteMessages(ids)
  if (res.data.code === 0) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.data.msg || '删除失败')
  }
}

function handleSelectionChange(rows: Message[]) {
  selectedIds.value = rows.map((r) => r.id)
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
          <span>留言板列表</span>
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
        <el-table-column prop="USERNAME" label="用户名" width="120" />
        <el-table-column prop="CONTENT" label="留言内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="REPLY" label="回复内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ADD_TIME" label="留言时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="success" link :icon="ChatLineRound" @click="handleReply(row)">
              回复
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
    <el-dialog v-model="detailVisible" title="留言详情" width="600px">
      <el-descriptions v-if="currentRow" :column="1" border>
        <el-descriptions-item label="用户名">{{ currentRow.USERNAME }}</el-descriptions-item>
        <el-descriptions-item label="留言内容">{{ currentRow.CONTENT }}</el-descriptions-item>
        <el-descriptions-item label="回复内容">{{ currentRow.REPLY || '暂无回复' }}</el-descriptions-item>
        <el-descriptions-item label="留言时间">{{ currentRow.ADD_TIME }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyVisible" title="回复留言" width="500px">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
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
