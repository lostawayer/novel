<template>
  <div class="messages-container">
    <div class="messages-box">
      <h2>留言板</h2>

      <!-- 发布留言 -->
      <el-card class="post-card">
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="4"
          placeholder="请输入留言内容"
        />
        <el-button type="primary" @click="handlePost" style="margin-top: 10px">
          发布留言
        </el-button>
      </el-card>

      <!-- 留言列表 -->
      <div class="message-list" v-loading="loading">
        <el-empty v-if="list.length === 0" description="暂无留言" />
        <el-card
          v-for="item in list"
          :key="item.id"
          class="message-item"
          shadow="hover"
        >
          <div class="message-header">
            <span class="username">{{ item.username }}</span>
            <span class="time">{{ item.addtime }}</span>
          </div>
          <div class="message-content">{{ item.content }}</div>
          <div v-if="item.reply" class="message-reply">
            <div class="reply-label">管理员回复：</div>
            <div class="reply-content">{{ item.reply }}</div>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <el-pagination
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
import { ElMessage } from 'element-plus'
import { get, post } from '@/utils/request'

const loading = ref(false)
const messageContent = ref('')
const list = ref<any[]>([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

// 获取留言列表
const getList = async () => {
  try {
    loading.value = true
    const res = await get('/messages/list', {
      page: page.value,
      limit: limit.value,
    })
    if (res.code === 0 && res.data) {
      list.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取留言失败:', error)
  } finally {
    loading.value = false
  }
}

// 发布留言
const handlePost = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }

  try {
    const res = await post('/messages/add', {
      content: messageContent.value,
    })
    if (res.code === 0) {
      ElMessage.success('发布成功')
      messageContent.value = ''
      getList()
    }
  } catch (error) {
    console.error('发布留言失败:', error)
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.messages-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.messages-box {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  h2 {
    margin-bottom: 20px;
  }
}

.post-card {
  margin-bottom: 20px;
}

.message-list {
  margin: 20px 0;
}

.message-item {
  margin-bottom: 15px;

  .message-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;

    .username {
      font-weight: bold;
      color: #409eff;
    }

    .time {
      color: #999;
      font-size: 12px;
    }
  }

  .message-content {
    color: #333;
    line-height: 1.6;
  }

  .message-reply {
    margin-top: 10px;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;

    .reply-label {
      font-weight: bold;
      color: #67c23a;
      margin-bottom: 5px;
    }

    .reply-content {
      color: #666;
    }
  }
}
</style>

