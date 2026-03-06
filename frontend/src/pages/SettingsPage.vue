<template>
  <div class="settings-page">
    <h1>账户设置</h1>
    
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      <el-form :model="user" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="user.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="user.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile" :loading="updating">保存更改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 2rem;">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form :model="passwordForm" label-width="100px" ref="pwdFormRef">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="updatePassword" :loading="updatingPwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 2rem;">
      <template #header>
        <span>主题设置</span>
      </template>
      <div class="theme-grid">
        <div v-for="(theme, key) in themeStore.themes" :key="key" 
          :class="['theme-item', { active: themeStore.currentTheme === key }]"
          @click="themeStore.setTheme(key)">
          <div class="theme-preview" :style="{ backgroundColor: theme.colors.primary }"></div>
          <p>{{ theme.name }}</p>
        </div>
      </div>
    </el-card>
    
    <el-card style="margin-top: 2rem;">
      <template #header>
        <span>其他设置</span>
      </template>
      <el-form label-width="150px">
        <el-form-item label="深色模式">
          <el-switch v-model="themeStore.isDarkMode" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useThemeStore } from '@/stores/themeStore'
import { useAuthStore } from '@/stores/authStore'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const updating = ref(false)
const updatingPwd = ref(false)

const user = reactive({
  username: '',
  email: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(async () => {
  if (!authStore.user) {
    await authStore.fetchUser()
  }
  user.username = authStore.user?.username || ''
  user.email = authStore.user?.email || ''
})

const updateProfile = async () => {
  if (!user.username || !user.email) {
    ElMessage.error('用户名和邮箱不能为空')
    return
  }
  
  updating.value = true
  try {
    const res = await api.put('/users/me', {
      username: user.username,
      email: user.email
    })
    // Update local store user
    authStore.user = res.data
    ElMessage.success('个人信息已更新')
  } catch (err: any) {
    // Error is handled by api interceptor mostly, but we can catch here too.
    console.error('Update profile failed', err)
  } finally {
    updating.value = false
  }
}

const updatePassword = async () => {
  if (!passwordForm.currentPassword || !passwordForm.newPassword) {
    ElMessage.error('请填写完整密码信息')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  
  updatingPwd.value = true
  try {
    await api.put('/users/me/password', {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    // Clear forms
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    
    // Force logout since token might be invalid or for security
    setTimeout(() => {
      authStore.logout()
    }, 1500)
  } catch (err: any) {
    console.error('Update password failed', err)
  } finally {
    updatingPwd.value = false
  }
}
</script>

<style scoped>
.settings-page h1 {
  margin-bottom: 1.5rem;
  font-size: 28px;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.theme-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.theme-item:hover {
  border-color: var(--color-primary);
  background: rgba(64, 158, 255, 0.1);
}

.theme-item.active {
  border-color: var(--color-primary);
  background: rgba(64, 158, 255, 0.2);
}

.theme-preview {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.theme-item p {
  margin: 0;
  font-size: 14px;
  text-align: center;
}
</style>
