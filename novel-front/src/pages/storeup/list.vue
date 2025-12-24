<template>
  <div class="storeup-container">
    <div class="storeup-box">
      <h2>我的收藏</h2>
      
      <div class="storeup-list" v-loading="loading">
        <template v-if="list.length > 0">
          <div 
            v-for="item in list" 
            :key="item.id" 
            class="storeup-item"
          >
            <el-image 
              :src="getImageUrl(item.picture)" 
              fit="cover" 
              class="cover"
              @click="toDetail(item)"
            />
            <div class="info">
              <div class="title" @click="toDetail(item)">{{ item.name }}</div>
              <div class="time">收藏时间：{{ item.addtime }}</div>
            </div>
            <div class="actions">
              <el-button type="primary" size="small" @click="toDetail(item)">
                查看详情
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(item)">
                取消收藏
              </el-button>
            </div>
          </div>
        </template>
        <el-empty v-else description="暂无收藏" />
      </div>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="page"
        v-model:page-size="limit"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="getList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post } from '@/utils/request'
import { useUserStore } from '@/store'
import { getImageUrl } from '@/common/system'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const list = ref<any[]>([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

// 获取收藏列表
const getList = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    loading.value = true
    const res = await get('/storeup/list', {
      page: page.value,
      limit: limit.value,
      userid: userStore.userInfo?.id,
      tablename: 'xiaoshuoxinxi'
    })
    if (res.code === 0 && res.data) {
      list.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查看详情
const toDetail = (item: any) => {
  router.push({
    path: '/index/xiaoshuoxinxiDetail',
    query: { id: item.refid }
  })
}

// 取消收藏
const handleDelete = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await post('/storeup/delete', {
      userid: userStore.userInfo?.id,
      refid: item.refid,
      tablename: 'xiaoshuoxinxi'
    })
    
    ElMessage.success('已取消收藏')
    getList()
  } catch (e: any) {
    if (e !== 'cancel') {
      console.error('取消收藏失败:', e)
    }
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.storeup-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.storeup-box {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  h2 {
    margin: 0 0 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
  }
}

.storeup-list {
  min-height: 200px;
}

.storeup-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
  
  &:hover {
    background: #fafafa;
  }
  
  &:last-child {
    border-bottom: none;
  }
  
  .cover {
    width: 80px;
    height: 110px;
    border-radius: 4px;
    cursor: pointer;
    flex-shrink: 0;
  }
  
  .info {
    flex: 1;
    
    .title {
      font-size: 16px;
      font-weight: 500;
      color: #333;
      cursor: pointer;
      margin-bottom: 8px;
      
      &:hover {
        color: #1890ff;
      }
    }
    
    .time {
      font-size: 13px;
      color: #999;
    }
  }
  
  .actions {
    display: flex;
    gap: 10px;
  }
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
