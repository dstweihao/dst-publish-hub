<template>
  <div class="settings-page">
    <h1>{{ $t('setting.title') }}</h1>
    
    <el-card>
      <template #header>
        <span>{{ $t('setting.profile') }}</span>
      </template>
      <el-form :model="user" label-position="top">
        <el-form-item :label="$t('setting.username')">
          <el-input v-model="user.username" disabled />
        </el-form-item>
        <el-form-item :label="$t('setting.email')">
          <el-input v-model="user.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile" :loading="updating">{{ $t('setting.saveChanges') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 2rem;">
      <template #header>
        <span>{{ $t('setting.updatePwd') }}</span>
      </template>
      <el-form :model="passwordForm" label-position="top" ref="pwdFormRef">
        <el-form-item :label="$t('setting.currentPwd')" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password />
        </el-form-item>
        <el-form-item :label="$t('setting.newPwd')" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item :label="$t('setting.confirmNewPwd')" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="updatePassword" :loading="updatingPwd">{{ $t('setting.updatePwd') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 2rem;">
      <template #header>
        <span>{{ $t('setting.themeSetting') }}</span>
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
        <span>{{ $t('setting.otherSetting') }}</span>
      </template>
      <el-form label-position="left">
        <el-form-item :label="$t('setting.darkMode')">
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
import { useI18n } from 'vue-i18n'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const { t } = useI18n()
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
    ElMessage.error(t('setting.profileEmpty'))
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
    ElMessage.success(t('setting.profileSuccess'))
  } catch (err: any) {
    // Error is handled by api interceptor mostly, but we can catch here too.
    console.error('Update profile failed', err)
  } finally {
    updating.value = false
  }
}

const updatePassword = async () => {
  if (!passwordForm.currentPassword || !passwordForm.newPassword) {
    ElMessage.error(t('setting.pwdEmpty'))
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error(t('setting.pwdMismatch'))
    return
  }
  
  updatingPwd.value = true
  try {
    await api.put('/users/me/password', {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success(t('setting.pwdSuccess'))
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
