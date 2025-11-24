<template>
  <div class="main-layout">
    <header class="header">
      <div class="header-left">
        <h1>文章发布平台</h1>
      </div>
      <div class="header-right">
        <el-button link @click="toggleDark">
          <el-icon v-if="isDarkMode"><Moon /></el-icon>
          <el-icon v-else><Sunny /></el-icon>
        </el-button>
        <el-dropdown>
          <el-avatar :src="authStore.user?.avatar" size="small" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateTo('settings')">设置</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">登出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    
    <div class="layout-container">
      <aside class="sidebar">
        <nav class="nav-menu">
          <router-link 
            v-for="item in menuItems" 
            :key="item.path"
            :to="item.path"
            :class="['nav-item', { active: $route.path === item.path }]"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </router-link>
        </nav>
      </aside>
      
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/themeStore'
import { useAuthStore } from '@/stores/authStore'
import { DocumentCopy, Edit, List, Setting, Share } from '@element-plus/icons-vue'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const router = useRouter()

const isDarkMode = computed(() => themeStore.isDarkMode)

const menuItems = [
  { path: '/', label: '仪表板', icon: DocumentCopy },
  { path: '/editor', label: '编辑文章', icon: Edit },
  { path: '/articles', label: '文章管理', icon: List },
  { path: '/platforms', label: '平台配置', icon: Share },
  { path: '/settings', label: '账户设置', icon: Setting }
]

const toggleDark = () => {
  themeStore.toggleDarkMode()
}

const navigateTo = (path: string) => {
  router.push(`/${path}`)
}

const handleLogout = () => {
  authStore.logout()
}
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--color-background);
  color: var(--color-text-primary);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: var(--color-background);
  border-bottom: 1px solid var(--color-border);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: var(--color-primary);
}

.header-right {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.layout-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  background: var(--color-background);
  border-right: 1px solid var(--color-border);
  overflow-y: auto;
  padding: 1rem 0;
}

.nav-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  color: var(--color-text-regular);
  text-decoration: none;
  transition: all 0.3s;
  cursor: pointer;
}

.nav-item:hover {
  background: rgba(64, 158, 255, 0.1);
  color: var(--color-primary);
}

.nav-item.active {
  background: rgba(64, 158, 255, 0.1);
  color: var(--color-primary);
  border-left: 3px solid var(--color-primary);
  padding-left: calc(1.5rem - 3px);
  font-weight: bold;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

@media (max-width: 768px) {
  .header {
    padding: 1rem;
  }

  .sidebar {
    width: 60px;
  }

  .nav-item span {
    display: none;
  }

  .nav-item {
    justify-content: center;
    padding: 0.75rem;
  }

  .content {
    padding: 1rem;
  }
}
</style>
