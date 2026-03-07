<template>
  <div class="main-layout">
    <header class="header">
      <div class="header-left">
        <img src="/logo.png" alt="Logo" class="layout-logo" />
        <h1>{{ $t('common.brand') }}</h1>
      </div>
      <div class="header-right">
        <!-- GitHub 仓库链接 -->
        <a href="https://github.com/dstweihao/dst-publish-hub" target="_blank" title="GitHub Repository" class="github-link" style="margin-right: 8px;">
          <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true"><path fill="currentColor" d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.604-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.464-1.11-1.464-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.578 9.578 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.025 2.747-1.025.546 1.379.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.161 22 16.416 22 12c0-5.523-4.477-10-10-10z"></path></svg>
        </a>

        <!-- 多语言切换 -->
        <el-dropdown @command="handleLanguageChange" style="margin-right: 16px;">
          <el-button link>
            <el-icon style="font-size: 18px;"><Memo /></el-icon>
            <span style="margin-left: 4px; font-weight: 500;">
              {{ currentLangLabel }}
            </span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="zh-CN" :disabled="locale === 'zh-CN'">简体中文</el-dropdown-item>
              <el-dropdown-item command="en-US" :disabled="locale === 'en-US'">English</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 深浅色切换 -->
        <el-button link @click="toggleDark">
          <el-icon v-if="isDarkMode"><Moon /></el-icon>
          <el-icon v-else><Sunny /></el-icon>
        </el-button>
        <el-dropdown>
          <div class="user-avatar-wrapper">
            <el-avatar 
              :src="authStore.user?.avatar || `https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=${authStore.user?.username || 'user'}&backgroundColor=eef2ff,c7d2fe`" 
              size="small" 
            />
            <span class="username-display">{{ authStore.user?.username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateTo('settings')">{{ $t('nav.settings') }}</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">{{ $t('nav.logout') }}</el-dropdown-item>
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
            <span>{{ $t(item.labelKey) }}</span>
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
import { DocumentCopy, Edit, List, Setting, Share, Moon, Sunny, Memo, Notebook } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const router = useRouter()
const { locale } = useI18n()

const isDarkMode = computed(() => themeStore.isDarkMode)

// 根据选择的 locale 计算显示文本
const currentLangLabel = computed(() => {
  return locale.value === 'en-US' ? 'En' : '中'
})

const handleLanguageChange = (lang: 'zh-CN' | 'en-US') => {
  locale.value = lang
  localStorage.setItem('app-locale', lang)
}

const menuItems = computed(() => [
  { path: '/', labelKey: 'nav.dashboard', icon: DocumentCopy },
  { path: '/editor', labelKey: 'nav.editor', icon: Edit },
  { path: '/articles', labelKey: 'nav.articles', icon: List },
  { path: '/platforms', labelKey: 'nav.platforms', icon: Share },
  { path: '/settings', labelKey: 'nav.settings', icon: Setting },
  { path: '/changelog', labelKey: 'nav.changelog', icon: Notebook }
])

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

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.layout-logo {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  object-fit: contain;
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: var(--color-primary);
}

.header-right {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.github-link {
  color: var(--color-text-regular);
  display: flex;
  align-items: center;
  transition: color 0.3s;
}

.github-link:hover {
  color: var(--color-primary);
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 20px;
  transition: background-color 0.2s;
}

.user-avatar-wrapper:hover {
  background-color: var(--color-border);
}

.username-display {
  font-weight: 500;
  font-size: 14px;
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
  background-color: var(--color-background);
  transition: background-color 0.3s ease;
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
