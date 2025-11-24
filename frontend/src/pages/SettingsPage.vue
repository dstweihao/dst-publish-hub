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
          <el-button type="primary" @click="updateProfile">保存更改</el-button>
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
import { reactive } from 'vue'
import { useThemeStore } from '@/stores/themeStore'
import { useAuthStore } from '@/stores/authStore'
import { ElMessage } from 'element-plus'

const themeStore = useThemeStore()
const authStore = useAuthStore()

const user = reactive({
  username: authStore.user?.username || '',
  email: authStore.user?.email || ''
})

const updateProfile = () => {
  ElMessage.success('个人信息已更新')
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
