<template>
  <div class="center-container">
    <div class="center-box">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="info">
          <el-form :model="userInfo" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.yonghuming" disabled />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="userInfo.xingming" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="userInfo.xingbie">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            <el-form-item label="头像">
              <FileUpload
                :fileUrls="userInfo.touxiang"
                @change="(val: string) => (userInfo.touxiang = val)"
              />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.youxiang" />
            </el-form-item>
            <el-form-item label="手机">
              <el-input v-model="userInfo.shouji" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdate">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" label-width="100px">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="我的收藏" name="collect">
          <p>我的收藏功能开发中...</p>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get, post } from '@/utils/request'
import { getUserInfo, setUserInfo } from '@/common/storage'
import FileUpload from '@/components/FileUpload.vue'
import { useUserStore } from '@/store'

const userStore = useUserStore()
const activeTab = ref('info')

const userInfo = reactive<any>({
  yonghuming: '',
  xingming: '',
  xingbie: '',
  touxiang: '',
  youxiang: '',
  shouji: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 获取用户信息
const loadUserInfo = () => {
  const info = getUserInfo()
  if (info) {
    Object.assign(userInfo, info)
  }
}

// 更新个人信息
const handleUpdate = async () => {
  try {
    const res = await post(`/${userStore.role}/update`, userInfo)
    if (res.code === 0) {
      setUserInfo(userInfo)
      userStore.setUserInfo(userInfo)
      ElMessage.success('更新成功')
    }
  } catch (error) {
    console.error('更新失败:', error)
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }

  try {
    const res = await post(`/${userStore.role}/update`, {
      id: userInfo.id,
      mima: passwordForm.newPassword,
    })
    if (res.code === 0) {
      ElMessage.success('密码修改成功，请重新登录')
      // 这里可以跳转到登录页
    }
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.center-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.center-box {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-select) {
  width: 100%;
}
</style>

