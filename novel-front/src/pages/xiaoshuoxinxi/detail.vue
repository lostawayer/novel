<template>
  <div class="detail-container">
    <!-- 书籍详情信息 -->
    <div class="novel-info" v-loading="detailLoading">
      <template v-if="novel">
        <div class="novel-cover">
          <el-image :src="getImageUrl(novel.fengmian)" fit="cover" />
        </div>
        <div class="novel-content">
          <h1 class="novel-title">{{ novel.xiaoshuomingcheng }}</h1>
          <div class="novel-meta">
            <span><el-icon><User /></el-icon> {{ novel.zuozhe }}</span>
            <span><el-icon><Collection /></el-icon> {{ novel.xiaoshuoleixing }}</span>
            <span><el-icon><View /></el-icon> {{ novel.clicknum || 0 }} 阅读</span>
          </div>
          <p class="novel-desc">{{ novel.jianjie || '暂无简介' }}</p>
          <div class="novel-actions">
            <el-button type="primary" @click="startReading">开始阅读</el-button>
            <el-button :type="isCollected ? 'warning' : 'default'" @click="handleCollect">
              <el-icon><Star /></el-icon>
              {{ isCollected ? '已收藏' : '收藏' }}
            </el-button>
            <!-- 购买书籍按钮 -->
            <el-button 
              v-if="novel.jiage && novel.jiage > 0 && !isPurchased && userStore.userInfo?.vip !== '是'"
              type="success" 
              @click="handleBuyBook"
              :loading="buying"
            >
              <el-icon><ShoppingCart /></el-icon>
              购买本书 ¥{{ novel.jiage }}
            </el-button>
            <el-tag v-if="isPurchased" type="success" size="large">已购买</el-tag>
            <el-tag v-if="userStore.userInfo?.vip === '是'" type="warning" size="large">VIP免费阅读</el-tag>
          </div>
        </div>
      </template>
      <el-empty v-else-if="!detailLoading" description="书籍信息加载失败" />
    </div>

    <div class="chapter-section">
      <h2>章节目录</h2>
      <div class="chapter-list" v-if="chapters.length > 0">
        <div 
          v-for="chapter in chapters" 
          :key="chapter.id" 
          class="chapter-item"
          @click="readChapter(chapter)"
        >
          <span class="chapter-title">
            第{{ chapter.zhangjiebianhao }}章 {{ chapter.zhangjiemingcheng }}
          </span>
          <el-tag v-if="chapter.vipRead === '是'" type="warning" size="small">VIP</el-tag>
        </div>
      </div>
      <el-empty v-else description="暂无章节" />
    </div>

    <div class="comment-section">
      <h2>读者评论</h2>
      <div class="comment-form" v-if="userStore.isLoggedIn">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
        />
        <el-button type="primary" @click="submitComment" :disabled="!commentContent.trim()">
          发表评论
        </el-button>
      </div>
      <div v-else class="login-tip">
        <router-link to="/login">登录</router-link> 后发表评论
      </div>
      
      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-avatar :src="getImageUrl(comment.avatarurl)" :size="40" />
          <div class="comment-content">
            <div class="comment-header">
              <span class="nickname">{{ comment.nickname }}</span>
              <span class="time">{{ comment.addtime }}</span>
            </div>
            <p class="content">{{ comment.content }}</p>
            <p v-if="comment.reply" class="reply">
              <el-tag size="small">作者回复</el-tag> {{ comment.reply }}
            </p>
          </div>
        </div>
        <el-empty v-if="comments.length === 0" description="暂无评论" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Collection, View, Star, ShoppingCart } from '@element-plus/icons-vue'
import { get, post } from '@/utils/request'
import { useUserStore } from '@/store'
import { getImageUrl } from '@/common/system'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const novel = ref<any>(null)
const chapters = ref<any[]>([])
const comments = ref<any[]>([])
const isCollected = ref(false)
const isPurchased = ref(false)
const buying = ref(false)
const commentContent = ref('')
const detailLoading = ref(false)

// 获取书籍详情
const loadNovelDetail = async () => {
  const id = route.query.id
  if (!id) {
    console.error('缺少书籍ID')
    return
  }
  
  detailLoading.value = true
  try {
    const res = await get(`/xiaoshuoxinxi/detail/${id}`)
    console.log('书籍详情响应:', res)
    if (res.code === 0 && res.data) {
      novel.value = res.data
    } else {
      console.error('获取书籍详情失败:', res.msg || '未知错误')
    }
  } catch (e) {
    console.error('获取书籍详情异常', e)
  } finally {
    detailLoading.value = false
  }
}

// 获取章节列表
const loadChapters = async () => {
  const id = route.query.id
  if (!id) return
  
  try {
    const res = await get(`/xiaoshuoxinxi/chapters/${id}`)
    console.log('章节列表:', res)
    if (res.code === 0 && res.data) {
      chapters.value = res.data
    }
  } catch (e) {
    console.error('获取章节列表失败', e)
  }
}

// 获取评论列表
const loadComments = async () => {
  const id = route.query.id
  if (!id) return
  
  try {
    const res = await get(`/xiaoshuoxinxi/comments/${id}`, { page: 1, limit: 20 })
    console.log('评论列表:', res)
    if (res.code === 0) {
      comments.value = res.data?.list || []
    }
  } catch (e) {
    console.error('获取评论失败', e)
  }
}

// 检查是否已收藏
const checkCollected = async () => {
  if (!userStore.isLoggedIn) return
  
  const id = route.query.id
  try {
    const res = await get('/storeup/list', {
      page: 1,
      limit: 1,
      userid: userStore.userInfo?.id,
      refid: id,
      tablename: 'xiaoshuoxinxi'
    })
    if (res.code === 0 && res.data?.total > 0) {
      isCollected.value = true
    }
  } catch (e) {
    console.error('检查收藏状态失败', e)
  }
}

// 检查是否已购买
const checkPurchased = async () => {
  if (!userStore.isLoggedIn) return
  
  const id = route.query.id
  try {
    const res = await get('/xiaoshuoxinxi/book/purchased', {
      userId: userStore.userInfo?.id,
      bookId: id
    })
    if (res.code === 0 && res.data?.purchased) {
      isPurchased.value = true
    }
  } catch (e) {
    console.error('检查购买状态失败', e)
  }
}

// 购买书籍
const handleBuyBook = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认购买《${novel.value?.xiaoshuomingcheng}》？价格：¥${novel.value?.jiage}`,
      '购买书籍',
      { confirmButtonText: '去支付', cancelButtonText: '取消', type: 'info' }
    )
    
    buying.value = true
    const res = await post('/alipay/createBookOrder', {
      userId: userStore.userInfo?.id,
      bookId: route.query.id
    })
    
    if (res.code === 0 && res.data?.payForm) {
      const div = document.createElement('div')
      div.innerHTML = res.data.payForm
      document.body.appendChild(div)
      const form = div.querySelector('form')
      if (form) form.submit()
    } else {
      ElMessage.error(res.msg || '创建订单失败')
    }
  } catch (e: any) {
    if (e !== 'cancel') {
      console.error('购买失败', e)
      ElMessage.error('购买失败')
    }
  } finally {
    buying.value = false
  }
}

// 开始阅读
const startReading = () => {
  if (chapters.value.length > 0) {
    readChapter(chapters.value[0])
  } else {
    ElMessage.warning('暂无章节')
  }
}

// 阅读章节
const readChapter = (chapter: any) => {
  router.push({
    path: '/index/xiaoshuoxinxiChapter',
    query: {
      id: chapter.id,
      novelId: route.query.id
    }
  })
}

// 收藏/取消收藏
const handleCollect = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isCollected.value) {
      // 取消收藏
      await post('/storeup/delete', {
        userid: userStore.userInfo?.id,
        refid: route.query.id,
        tablename: 'xiaoshuoxinxi'
      })
      isCollected.value = false
      ElMessage.success('已取消收藏')
    } else {
      // 添加收藏
      await post('/storeup/add', {
        userid: userStore.userInfo?.id,
        refid: route.query.id,
        tablename: 'xiaoshuoxinxi',
        name: novel.value?.xiaoshuomingcheng,
        picture: novel.value?.fengmian?.split(',')[0],
        inteltype: novel.value?.xiaoshuoleixing
      })
      isCollected.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    console.error('操作失败', e)
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) return
  
  try {
    const res = await post('/xiaoshuoxinxi/comment/add', {
      refid: route.query.id,
      userid: userStore.userInfo?.id,
      nickname: userStore.userInfo?.xingming || userStore.userInfo?.yonghuming || '匿名用户',
      avatarurl: userStore.userInfo?.touxiang || '',
      content: commentContent.value
    })
    if (res.code === 0) {
      ElMessage.success('评论成功')
      commentContent.value = ''
      loadComments()
    } else {
      ElMessage.error(res.msg || '评论失败')
    }
  } catch (e) {
    ElMessage.error('评论失败')
  }
}

onMounted(() => {
  loadNovelDetail()
  loadChapters()
  loadComments()
  checkCollected()
  checkPurchased()
  
  // 处理购买回调
  console.log('页面加载，完整URL:', window.location.href)
  console.log('URL参数:', route.query)
  if (route.query.buyResult === 'success') {
    // 支付宝回调的订单号参数名是 out_trade_no
    // 可能是数组（URL中有重复参数），取第一个值
    let outTradeNo = route.query.out_trade_no
    if (Array.isArray(outTradeNo)) {
      outTradeNo = outTradeNo[0]
    }
    outTradeNo = (outTradeNo as string) || ''
    
    console.log('购买回调，订单号:', outTradeNo)
    if (outTradeNo) {
      console.log('调用确认接口: /alipay/bookReturn?out_trade_no=' + outTradeNo)
      get('/alipay/bookReturn', { out_trade_no: outTradeNo }).then((res) => {
        console.log('确认购买结果:', res)
        if (res.code === 0) {
          isPurchased.value = true
          ElMessage.success('🎉 购买成功！现在可以阅读全部章节了')
        } else {
          checkPurchased()
          ElMessage.warning(res.msg || '订单确认中，请稍后刷新页面')
        }
      }).catch(e => {
        console.error('确认购买失败:', e)
        checkPurchased()
        ElMessage.warning('订单确认中，请稍后刷新页面')
      })
    } else {
      console.log('没有订单号，刷新购买状态')
      checkPurchased()
      ElMessage.success('🎉 购买成功！')
    }
  }
})
</script>

<style lang="scss" scoped>
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.novel-info {
  display: flex;
  gap: 30px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.novel-cover {
  flex-shrink: 0;
  width: 200px;
  height: 280px;
  border-radius: 8px;
  overflow: hidden;
  
  .el-image {
    width: 100%;
    height: 100%;
  }
}

.novel-content {
  flex: 1;
  
  .novel-title {
    font-size: 24px;
    margin: 0 0 15px;
    color: #333;
  }
  
  .novel-meta {
    display: flex;
    gap: 20px;
    color: #666;
    font-size: 14px;
    margin-bottom: 20px;
    
    span {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
  
  .novel-desc {
    color: #666;
    line-height: 1.8;
    margin-bottom: 25px;
  }
  
  .novel-actions {
    display: flex;
    gap: 15px;
  }
}

.chapter-section, .comment-section {
  background: #fff;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  
  h2 {
    font-size: 18px;
    margin: 0 0 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}

.chapter-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 10px;
}

.chapter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f9f9f9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background: #e6f7ff;
    color: #1890ff;
  }
  
  .chapter-title {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.comment-form {
  margin-bottom: 20px;
  
  .el-button {
    margin-top: 10px;
  }
}

.login-tip {
  text-align: center;
  padding: 20px;
  color: #999;
  
  a {
    color: #1890ff;
  }
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.comment-content {
  flex: 1;
  
  .comment-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;
    
    .nickname {
      font-weight: 500;
      color: #333;
    }
    
    .time {
      font-size: 12px;
      color: #999;
    }
  }
  
  .content {
    color: #666;
    line-height: 1.6;
    margin: 0;
  }
  
  .reply {
    margin-top: 10px;
    padding: 10px;
    background: #f9f9f9;
    border-radius: 4px;
    font-size: 14px;
    color: #666;
  }
}
</style>
