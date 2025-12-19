<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getConfigPage, updateConfig, type Config } from '@/api/admin/configApi'

const loading = ref(false)
const tableData = ref<Config[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 编辑弹窗
const dialogVisible = ref(false)
const dialogForm = ref({ id: 0, name: '', value: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await getConfigPage({
      page: currentPage.value,
      limit: pageSize.value
    })
    if (res.data.code === 0) {
      tableData.value = res.data.data.list
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleEdit(row: Config) {
  dialogForm.value = {
    id: row.id,
    name: row.NAME,
    value: row.VALUE || ''
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const res = await updateConfig({ id: dialogForm.value.id, value: dialogForm.value.value })
  if (res.data.code === 0) {
    ElMessage.success('更新成功')
    dialogVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.data.msg || '更新失败')
  }
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>轮播图配置</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="NAME" label="配置名称" width="150" />
        <el-table-column prop="VALUE" label="配置值" min-width="300">
          <template #default="{ row }">
            <el-image
              v-if="row.VALUE && row.VALUE.includes('upload')"
              :src="'/' + row.VALUE"
              style="width: 100px; height: 60px"
              fit="cover"
            />
            <span v-else>{{ row.VALUE }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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
    <el-dialog v-model="dialogVisible" title="编辑配置" width="500px">
      <el-form :model="dialogForm" label-width="80px">
        <el-form-item label="配置名称">
          <el-input v-model="dialogForm.name" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input v-model="dialogForm.value" placeholder="请输入配置值" />
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
.table-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
