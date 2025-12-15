<template>
  <div class="home-container">
    <!-- 推荐小说 -->
    <div class="recommend-section">
      <div class="section-title">
        <span>小说推荐</span>
      </div>

      <div class="recommend-list" v-loading="loading">
        <div
          v-for="item in recommendList"
          :key="item.id"
          class="recommend-item"
          @click="toDetail(item)"
        >
          <el-image
            :src="getImageUrl(item.fengmian)"
            fit="cover"
            class="item-cover"
          ></el-image>
          <div class="item-info">
            <div class="item-title">{{ item.xiaoshuomingcheng }}</div>
            <div class="item-category">{{ item.xiaoshuoleixing }}</div>
            <div class="item-author">作者：{{ item.zuozhe }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新小说 -->
    <div class="latest-section">
      <div class="section-title">
        <span>最新发布</span>
      </div>

      <div class="latest-list" v-loading="loading">
        <div
          v-for="item in latestList"
          :key="item.id"
          class="latest-item"
          @click="toDetail(item)"
        >
          <el-image
            :src="getImageUrl(item.fengmian)"
            fit="cover"
            class="item-cover"
          ></el-image>
          <div class="item-info">
            <div class="item-title">{{ item.xiaoshuomingcheng }}</div>
            <div class="item-category">{{ item.xiaoshuoleixing }}</div>
            <div class="item-price">¥{{ item.jiage }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门小说 -->
    <div class="hot-section">
      <div class="section-title">
        <span>热门排行</span>
      </div>

      <div class="hot-list" v-loading="loading">
        <div
          v-for="(item, index) in hotList"
          :key="item.id"
          class="hot-item"
          @click="toDetail(item)"
        >
          <div class="rank-number">{{ index + 1 }}</div>
          <el-image
            :src="getImageUrl(item.fengmian)"
            fit="cover"
            class="item-cover"
          ></el-image>
          <div class="item-info">
            <div class="item-title">{{ item.xiaoshuomingcheng }}</div>
            <div class="item-stats">
              <span>点击：{{ item.clicknum || 0 }}</span>
              <span>收藏：{{ item.storeupnum || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { getImageUrl } from '@/common/system'
import type { Novel } from '@/types'

const router = useRouter()

const loading = ref(false)
const recommendList = ref<Novel[]>([])
const latestList = ref<Novel[]>([])
const hotList = ref<Novel[]>([])

// 获取推荐小说
const getRecommendList = async () => {
  try {
    loading.value = true
    const res = await get('/xiaoshuoxinxi/list', {
      page: 1,
      limit: 5,
      sort: 'id',
      order: 'desc',
    })
    if (res.code === 0 && res.data) {
      recommendList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取推荐小说失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取最新小说
const getLatestList = async () => {
  try {
    const res = await get('/xiaoshuoxinxi/list', {
      page: 1,
      limit: 6,
      sort: 'addtime',
      order: 'desc',
    })
    if (res.code === 0 && res.data) {
      latestList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取最新小说失败:', error)
  }
}

// 获取热门小说
const getHotList = async () => {
  try {
    const res = await get('/xiaoshuoxinxi/list', {
      page: 1,
      limit: 10,
      sort: 'clicknum',
      order: 'desc',
    })
    if (res.code === 0 && res.data) {
      hotList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取热门小说失败:', error)
  }
}

// 查看详情
const toDetail = (item: Novel) => {
  router.push({
    path: '/index/xiaoshuoxinxiDetail',
    query: { id: item.id },
  })
}

onMounted(() => {
  getRecommendList()
  getLatestList()
  getHotList()
})
</script>

<style lang="scss" scoped>
.home-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  margin: 30px 0 20px;
  text-align: center;

  span {
    font-size: 28px;
    font-weight: bold;
    color: #333;
    padding-bottom: 10px;
    border-bottom: 3px solid #409eff;
    display: inline-block;
  }
}

// 推荐小说
.recommend-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.recommend-item {
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }

  .item-cover {
    width: 100%;
    height: 280px;
  }

  .item-info {
    padding: 10px;

    .item-title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin-bottom: 5px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-category,
    .item-author {
      font-size: 14px;
      color: #666;
      margin: 3px 0;
    }
  }
}

// 最新小说
.latest-list {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 15px;
}

.latest-item {
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .item-cover {
    width: 100%;
    height: 220px;
  }

  .item-info {
    padding: 10px;

    .item-title {
      font-size: 14px;
      font-weight: bold;
      color: #333;
      margin-bottom: 5px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-category {
      font-size: 12px;
      color: #999;
    }

    .item-price {
      font-size: 16px;
      color: #f56c6c;
      font-weight: bold;
      margin-top: 5px;
    }
  }
}

// 热门排行
.hot-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.hot-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #f5f5f5;
  }

  .rank-number {
    width: 30px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    font-weight: bold;
    font-size: 16px;
    margin-right: 15px;
    color: #999;

    &:nth-child(1) {
      color: #f56c6c;
    }
  }

  .item-cover {
    width: 60px;
    height: 80px;
    margin-right: 15px;
    border-radius: 4px;
  }

  .item-info {
    flex: 1;

    .item-title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-stats {
      font-size: 14px;
      color: #999;

      span {
        margin-right: 20px;
      }
    }
  }
}

@media (max-width: 768px) {
  .recommend-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .latest-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>

