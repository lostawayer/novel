<template>
  <div class="chapter-container">
    <div class="chapter-header">
      <div class="nav-info">
        <router-link :to="{ path: '/index/xiaoshuoxinxiDetail', query: { id: novelId } }">
          {{ chapter?.xiaoshuomingcheng || '返回书籍' }}
        </router-link>
        <span class="separator">/</span>
        <span v-if="chapter">第{{ chapter.zhangjiebianhao }}章 {{ chapter.zhangjiemingcheng }}</span>
        <span v-else>加载中...</span>
      </div>
    </div>

    <div v-if="loading" class="loading-box">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <div class="chapter-content" v-else-if="chapter">
      <div v-if="chapter.needVip" class="vip-block">
        <div class="vip-icon">
          <el-icon :size="60" color="#e6a23c"><Lock /></el-icon>
        </div>
        <h2>VIP专享章节</h2>
        <p>{{ chapter.vipMsg }}</p>
        <div class="vip-actions">
          <el-button type="warning" size="large" @click="goToVip">开通VIP会员</el-button>
          <el-button size="large" @click="goBack">返回目录</el-button>
        </div>
      </div>

      <div v-else class="content-body">
        <h1 class="chapter-title">第{{ chapter.zhangjiebianhao }}章 {{ chapter.zhangjiemingcheng }}</h1>
        <div class="content-text" v-html="chapter.neirong"></div>
      </div>
    </div>

    <el-empty v-else description="章节不存在" />

    <div class="chapter-footer" v-if="chapter && !chapter.needVip">
      <el-button :disabled="!prevChapter" @click="goPrev">上一章</el-button>
      <el-button @click="goBack">目录</el-button>
      <el-button :disabled="!nextChapter" @click="goNext">下一章</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Loading } from '@element-plus/icons-vue'
import { get } from '@/utils/request'
import { useUserStore } from '@/store'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const chapter = ref<any>(null)
const chapters = ref<any[]>([])
const novelId = ref<string>('')

const currentIndex = computed(() => {
  if (!chapter.value || chapters.value.length === 0) return -1
  return chapters.value.findIndex(c => c.id == chapter.value.id)
})

const prevChapter = computed(() => {
  if (currentIndex.value <= 0) return null
  return chapters.value[currentIndex.value - 1]
})

const nextChapter = computed(() => {
  if (currentIndex.value < 0 || currentIndex.value >= chapters.value.length - 1) return null
  return chapters.value[currentIndex.value + 1]
})

const loadChapter = async () => {
  const id = route.query.id
  novelId.value = (route.query.novelId as string) || ''
  if (!id) {
    ElMessage.error('缺少章节ID')
    return
  }
  
  loading.value = true
  try {
    const userId = userStore.userInfo?.id
    const params = userId ? { userId } : {}
    const res = await get(`/xiaoshuoxinxi/chapter/${id}`, params)
    console.log('章节数据:', res)
    if (res.code === 0 && res.data) {
      chapter.value = res.data
      // 如果没有传novelId，从章节数据中获取
      if (!novelId.value && res.data.novelId) {
        novelId.value = String(res.data.novelId)
      }
    } else {
      ElMessage.error(res.msg || '获取章节失败')
    }
  } catch (e) {
    console.error('获取章节失败:', e)
    ElMessage.error('获取章节失败')
  } finally {
    loading.value = false
  }
}

const loadChapterList = async () => {
  if (!novelId.value) return
  try {
    const res = await get(`/xiaoshuoxinxi/chapters/${novelId.value}`)
    if (res.code === 0) {
      chapters.value = res.data || []
    }
  } catch (e) {
    console.error('获取章节列表失败', e)
  }
}

const goToVip = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/index/center')
}

const goBack = () => {
  router.push({ path: '/index/xiaoshuoxinxiDetail', query: { id: novelId.value } })
}

const goPrev = () => {
  if (prevChapter.value) {
    router.push({ path: '/index/xiaoshuoxinxiChapter', query: { id: prevChapter.value.id, novelId: novelId.value } })
  }
}

const goNext = () => {
  if (nextChapter.value) {
    router.push({ path: '/index/xiaoshuoxinxiChapter', query: { id: nextChapter.value.id, novelId: novelId.value } })
  }
}

watch(() => route.query.id, (newId) => {
  if (newId) {
    loadChapter()
    window.scrollTo(0, 0)
  }
})

onMounted(() => {
  loadChapter()
  loadChapterList()
})
</script>

<style lang="scss" scoped>
.chapter-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background: #fff;
}
.loading-box {
  text-align: center;
  padding: 100px 0;
  color: #999;
  p { margin-top: 15px; }
}
.chapter-header {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
  .nav-info {
    font-size: 14px;
    color: #666;
    a { color: #1890ff; text-decoration: none; }
    .separator { margin: 0 10px; color: #ddd; }
  }
}
.vip-block {
  text-align: center;
  padding: 80px 20px;
  h2 { color: #333; margin: 20px 0 15px; }
  p { color: #666; margin-bottom: 30px; }
  .vip-actions { display: flex; justify-content: center; gap: 15px; }
}
.content-body {
  .chapter-title {
    text-align: center;
    font-size: 22px;
    color: #333;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px dashed #eee;
  }
  .content-text {
    font-size: 18px;
    line-height: 2;
    color: #333;
    :deep(p) { text-indent: 2em; margin-bottom: 1em; }
  }
}
.chapter-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 40px 0;
  border-top: 1px solid #eee;
  margin-top: 40px;
  .el-button { min-width: 100px; }
}
</style>
