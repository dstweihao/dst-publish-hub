<template>
  <div class="platforms-page">
    <div class="page-header">
      <h1>{{ $t('platform.title') }}</h1>
      <p class="subtitle">连接您的社交媒体账号，开启一键群发之旅 / Connect your social media accounts</p>
    </div>
    
    <el-row :gutter="20">
      <el-col v-for="platform in platforms" :key="platform.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="platform-card" :class="{ 'is-connected': platform.connected }">
          <div class="platform-header">
            <div class="platform-icon-wrapper" :style="{ backgroundColor: platform.color + '20', color: platform.color }">
              <el-icon :size="24">
                <component :is="platform.icon" />
              </el-icon>
            </div>
            <div class="platform-info">
              <span class="platform-name">{{ platform.name }}</span>
              <el-tag size="small" :type="platform.connected ? 'success' : 'info'" border>
                {{ platform.connected ? $t('platform.status.bound') : $t('platform.status.unbound') }}
              </el-tag>
            </div>
          </div>
          
          <div class="platform-content">
            <p class="platform-desc">{{ platform.description }}</p>
            <div class="actions">
              <el-button v-if="!platform.connected" type="primary" plain @click="connectPlatform(platform)">
                {{ $t('platform.bindBtn') }}
              </el-button>
              <el-button v-else type="danger" plain @click="disconnectPlatform(platform)">
                {{ $t('platform.unbindBtn') }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="dialogVisible" :title="'连接 ' + (selectedPlatform?.name || '')" width="400px" custom-class="platform-dialog">
      <el-form :model="accountForm" label-position="top">
        <el-form-item label="账户标识 (用于区分)">
          <el-input v-model="accountForm.accountName" placeholder="例如：我的知乎小号" />
        </el-form-item>
        <el-form-item label="访问令牌 / Cookie">
          <el-input v-model="accountForm.token" type="password" show-password placeholder="请输入授权令牌" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer-actions">
          <el-button type="info" plain :loading="assisting" @click="startLoginAssist" icon="Promotion">
            Browser Assist
          </el-button>
          <div class="standard-actions">
            <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
            <el-button type="primary" @click="saveAccount" :loading="submitting" :color="selectedPlatform?.color">
              {{ $t('common.confirm') }}
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import { SUPPORTED_PLATFORMS } from '@/config/platforms'

const dialogVisible = ref(false)
const selectedPlatform = ref<any>(null)
const loading = ref(false)
const submitting = ref(false)
const assisting = ref(false)
const accountForm = reactive({
  accountName: '',
  token: ''
})

const platforms = ref(SUPPORTED_PLATFORMS.map(p => ({ ...p, connected: false })))

const startLoginAssist = async () => {
  if (!selectedPlatform.value) return
  
  assisting.value = true
  ElMessage.info({
    message: `即将打开浏览器，请在弹出的窗口中登录 ${selectedPlatform.value.name}`,
    duration: 5000
  })
  
  try {
    const res: any = await api.post(`/automation/login-assist?platform=${selectedPlatform.value.name}`, null, {
      timeout: 300000 // 5 minutes timeout for login assist
    })
    if (res.success && res.data) {
      accountForm.token = res.data.token
      accountForm.accountName = res.data.username || accountForm.accountName
      ElMessage.success('凭证捕获成功！已自动填充。')
    } else if (!res.success) {
      ElMessage.error(res.message || '自动化登录失败，请检查后端输出')
    }
  } catch (err: any) {
    console.error('Login assist failed', err)
    // 错误信息已由拦截器处理
  } finally {
    assisting.value = false
  }
}

const fetchPlatforms = async () => {
  loading.value = true
  try {
    const res = await api.get('/platforms')
    const connectedList = res.data || []
    
    // Reset all
    platforms.value.forEach(p => p.connected = false)
    
    // Mark connected
    for (const connected of connectedList) {
      const match = platforms.value.find(p => p.name === connected.platform)
      if (match) {
        match.connected = true
      }
    }
  } catch (err: any) {
    console.error('Failed to fetch platforms', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPlatforms()
})

const connectPlatform = (platform: any) => {
  selectedPlatform.value = platform
  accountForm.accountName = ''
  accountForm.token = ''
  dialogVisible.value = true
}

const disconnectPlatform = async (platform: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要断开与 ${platform.name} 的连接吗？`,
      '警告',
      { confirmButtonText: '确定断开', cancelButtonText: '取消', type: 'warning' }
    )
    
    await api.delete(`/platforms/${platform.name}`)
    platform.connected = false
    ElMessage.success(`${platform.name} 已断开连接`)
  } catch (err: any) {
    if (err !== 'cancel') {
      console.error('Failed to disconnect platform', err)
    }
  }
}

const saveAccount = async () => {
  if (!accountForm.accountName || !accountForm.token) {
    ElMessage.error('请填写所有字段')
    return
  }
  
  if (selectedPlatform.value) {
    submitting.value = true
    try {
      await api.post('/platforms', {
        platform: selectedPlatform.value.name,
        username: accountForm.accountName,
        accessToken: accountForm.token
      })
      
      selectedPlatform.value.connected = true
      ElMessage.success(`${selectedPlatform.value.name} 已成功连接`)
      dialogVisible.value = false
    } catch (err: any) {
      console.error('Failed to connect platform', err)
    } finally {
      submitting.value = false
    }
  }
}
</script>

<style scoped>
.platforms-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2.5rem;
}

.platforms-page h1 {
  margin-bottom: 0.5rem;
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(45deg, var(--el-color-primary), var(--el-color-success));
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  color: var(--el-text-color-secondary);
  font-size: 16px;
}

.platform-card {
  margin-bottom: 20px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
}

.platform-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
}

.platform-card.is-connected {
  border-color: var(--el-color-success-light-5);
}

.platform-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.platform-icon-wrapper {
  width: 54px;
  height: 54px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.platform-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.platform-name {
  font-weight: 700;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.platform-content {
  text-align: left;
}

.platform-desc {
  color: var(--el-text-color-secondary);
  height: 40px;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions .el-button {
  width: 100%;
  border-radius: 8px;
  font-weight: 600;
}

/* 适配深色模式 */
:deep(.html.dark) .platform-card {
  background-color: var(--el-bg-color-overlay);
}

:deep(.platform-dialog) {
  border-radius: 16px;
}

.dialog-footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.standard-actions {
  display: flex;
  gap: 12px;
}
</style>
