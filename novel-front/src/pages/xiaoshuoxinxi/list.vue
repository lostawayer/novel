<template>
  <div class="novel-list-container">
    <div class="novel-box">
      <h2>小说列表</h2>

      <!-- 搜索区域 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="小说名称">
          <el-input
            v-model="searchForm.xiaoshuomingcheng"
            placeholder="请输入小说名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="小说类型">
          <el-select v-model="searchForm.xiaoshuoleixing" placeholder="请选择" clearable>
            <el-option label="全部" value="" />
            <!-- 可以从接口获取类型列表 -->
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 小说列表 -->
      <div class="novel-grid" v-loading="loading">
        <div
          v-for="item in list"
          :key="item.id"
          class="novel-card"
          @click="toDetail(item)"
        >
          <el-image :src="getImageUrl(item.fengmian)" fit="cover" class="cover" />
          <div class="info">
            <div class="title">{{ item.xiaoshuomingcheng }}</div>
            <div class="category">{{ item.xiaoshuoleixing }}</div>
            <div class="author">作者：{{ item.zuozhe }}</div>
            <div class="price">¥{{ item.jiage }}</div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="limit"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="getList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { getImageUrl } from '@/common/system'

const router = useRouter()

const loading = ref(false)
const list = ref<any[]>([])
const page = ref(1)
const limit = ref(12)
const total = ref(0)

const searchForm = reactive({
  xiaoshuomingcheng: '',
  xiaoshuoleixing: '',
})

// 获取列表
const getList = async () => {
  try {
    loading.value = true
    const res = await get('/xiaoshuoxinxi/list', {
      page: page.value,
      limit: limit.value,
      ...searchForm,
    })
    if (res.code === 0 && res.data) {
      list.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    xiaoshuomingcheng: '',
    xiaoshuoleixing: '',
  })
  handleSearch()
}

// 查看详情
const toDetail = (item: any) => {
  router.push({
    path: '/index/xiaoshuoxinxiDetail',
    query: { id: item.id },
  })
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.novel-list-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.novel-box {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-form {
  margin-bottom: 20px;
}

.novel-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin: 20px 0;
}

.novel-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }

  .cover {
    width: 100%;
    height: 300px;
  }

  .info {
    padding: 15px;

    .title {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .category,
    .author {
      font-size: 14px;
      color: #666;
      margin: 5px 0;
    }

    .price {
      font-size: 18px;
      color: #f56c6c;
      font-weight: bold;
      margin-top: 10px;
    }
  }
}
</style>

