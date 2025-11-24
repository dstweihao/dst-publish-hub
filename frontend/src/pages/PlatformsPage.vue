<template>
  <div class="platforms-page">
    <h1>平台配置</h1>
    
    <el-row :gutter="20">
      <el-col v-for="platform in platforms" :key="platform.id" :xs="24" :sm="12" :md="8">
        <el-card class="platform-card">
          <template #header>
            <div class="card-header">
              <span class="platform-name">{{ platform.name }}</span>
              <el-switch v-model="platform.connected" />
            </div>
          </template>
          
          <div class="platform-content">
            <p class="platform-desc">{{ platform.description }}</p>
            <el-button v-if="!platform.connected" type="primary" size="small" @click="connectPlatform(platform)">
              连接账户
            </el-button>
            <el-button v-else type="warning" size="small" @click="disconnectPlatform(platform)">
              断开连接
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="dialogVisible" title="连接平台账户" width="400px">
      <el-form :model="accountForm">
        <el-form-item label="账户名">
          <el-input v-model="accountForm.accountName" placeholder="请输入账户名" />
        </el-form-item>
        <el-form-item label="API Token">
          <el-input v-model="accountForm.token" type="password" placeholder="请输入API Token" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAccount">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)
const selectedPlatform = ref<any>(null)
const accountForm = reactive({
  accountName: '',
  token: ''
})

const platforms = ref([
  { id: 1, name: '微信公众号', description: '中国最大的公众号平台', connected: false },
  { id: 2, name: '稀土掘金', description: '技术社区', connected: false },
  { id: 3, name: '博客园', description: '中文博客社区', connected: false },
  { id: 4, name: '知乎', description: '中文问答社区', connected: false },
  { id: 5, name: 'CSDN', description: '中国最大的IT社区', connected: false },
  { id: 6, name: '今日头条', description: '内容聚合平台', connected: false },
  { id: 7, name: '简书', description: '内容创作平台', connected: false },
  { id: 8, name: 'Medium', description: '国际内容平台', connected: false },
  { id: 9, name: 'Hashnode', description: '开发者博客', connected: false },
  { id: 10, name: 'Dev.to', description: '开发者社区', connected: false },
  { id: 11, name: '开源中国', description: '开源社区', connected: false },
  { id: 12, name: '思否', description: '技术问答', connected: false },
  { id: 13, name: '百家号', description: '百度内容平台', connected: false },
  { id: 14, name: '小红书', description: '生活方式分享', connected: false },
  { id: 15, name: '豆瓣', description: '电影评论社区', connected: false },
  { id: 16, name: '微博', description: '社交媒体', connected: false }
])

const connectPlatform = (platform: any) => {
  selectedPlatform.value = platform
  accountForm.accountName = ''
  accountForm.token = ''
  dialogVisible.value = true
}

const disconnectPlatform = (platform: any) => {
  platform.connected = false
  ElMessage.success(`${platform.name} 已断开连接`)
}

const saveAccount = () => {
  if (!accountForm.accountName || !accountForm.token) {
    ElMessage.error('请填写所有字段')
    return
  }
  
  if (selectedPlatform.value) {
    selectedPlatform.value.connected = true
    ElMessage.success(`${selectedPlatform.value.name} 已连接`)
  }
  
  dialogVisible.value = false
}
</script>

<style scoped>
.platforms-page h1 {
  margin-bottom: 2rem;
  font-size: 28px;
}

.platform-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.platform-name {
  font-weight: bold;
  font-size: 16px;
}

.platform-content {
  text-align: center;
}

.platform-desc {
  color: var(--color-text-secondary);
  margin-bottom: 1rem;
  font-size: 14px;
}
</style>
