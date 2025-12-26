<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

interface Order {
  id: number
  ORDER_NO: string
  USER_ID: number
  USERNAME?: string
  ORDER_TYPE: string
  VIP_TYPE: string
  AMOUNT: number
  DAYS: number
  STATUS: string
  TRADE_NO: string
  CREATE_TIME: string
  PAY_TIME: string
  BOOK_ID?: number
  BOOK_NAME?: string
}

const loading = ref(false)
const tableData = ref<Order[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  orderNo: '',
  status: '',
  username: ''
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/list', {
      params: {
        page: currentPage.value,
        limit: pageSize.value,
        ...searchForm.value
      }
    })
    if (res.data.code === 0) {
      tableData.value = res.data.data?.list || []
      total.value = res.data.data?.total || 0
    }
  } catch (e) {
    console.error('加载订单失败', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = { orderNo: '', status: '', username: '' }
  handleSearch()
}

const handlePageChange = () => {
  loadData()
}

// 格式化VIP类型
const formatVipType = (type: string) => {
  const map: Record<string, string> = {
    month: '月度会员',
    quarter: '季度会员',
    year: '年度会员'
  }
  return map[type] || type
}

// 格式化状态
const formatStatus = (status: string) => {
  const map: Record<string, { text: string; type: string }> = {
    PENDING: { text: '待支付', type: 'warning' },
    PAID: { text: '已支付', type: 'success' },
    CANCELLED: { text: '已取消', type: 'info' }
  }
  return map[status] || { text: status, type: 'info' }
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已取消" value="CANCELLED" />
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
          <span>订单列表</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="ORDER_NO" label="订单号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="USERNAME" label="用户" width="100" />
        <el-table-column label="订单类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.ORDER_TYPE === 'VIP'" type="warning" size="small">VIP会员</el-tag>
            <el-tag v-else type="success" size="small">购买书籍</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="140">
          <template #default="{ row }">
            <span v-if="row.ORDER_TYPE === 'VIP'">
              {{ formatVipType(row.VIP_TYPE) }} ({{ row.DAYS }}天)
            </span>
            <span v-else>《{{ row.BOOK_NAME }}》</span>
          </template>
        </el-table-column>
        <el-table-column prop="AMOUNT" label="金额" width="90" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.AMOUNT }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="STATUS" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="formatStatus(row.STATUS).type as any" size="small">
              {{ formatStatus(row.STATUS).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="CREATE_TIME" label="下单时间" width="160" align="center" />
        <el-table-column prop="PAY_TIME" label="支付时间" width="160" align="center">
          <template #default="{ row }">
            {{ row.PAY_TIME || '-' }}
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
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.price {
  color: #e6a23c;
  font-weight: bold;
}
</style>
