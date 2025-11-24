<template>
  <el-container class="h-screen bg-gray-950">
    <el-header class="bg-gray-900 border-b border-gray-800">
      <div class="flex items-center justify-between h-full px-6">
        <div class="flex items-center gap-3">
          <div class="text-2xl font-bold text-blue-500">✨ ArticleHub</div>
        </div>
        <div class="flex items-center gap-4">
          <el-dropdown @command="handleCommand">
            <div class="flex items-center gap-2 cursor-pointer text-gray-300 hover:text-white">
              <el-avatar 
                :src="auth.user?.avatar || '/placeholder.svg?height=32&width=32'"
                size="small"
              />
              <span>{{ auth.user?.username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <el-container class="flex-1 overflow-hidden">
      <!-- 侧边栏 -->
      <el-aside width="250px" class="bg-gray-900 border-r border-gray-800 overflow-y-auto">
        <div class="p-4">
          <el-menu 
            :default-active="$route.name || 'dashboard'"
            class="border-0 bg-gray-900 text-gray-300"
            active-text-color="#3b82f6"
            @select="handleMenuSelect"
          >
            <el-menu-item index="dashboard" @click="$router.push('/')">
              <el-icon><home-filled /></el-icon>
              <span>仪表板</span>
            </el-menu-item>
            <el-menu-item index="editor" @click="$router.push('/editor')">
              <el-icon><document-add /></el-icon>
              <span>新建文章</span>
            </el-menu-item>
            <el-menu-item index="articles" @click="$router.push('/articles')">
              <el-icon><document-copy /></el-icon>
              <span>文章管理</span>
            </el-menu-item>
            <el-menu-item index="platforms" @click="$router.push('/platforms')">
              <el-icon><link /></el-icon>
              <span>平台配置</span>
            </el-menu-item>
            <el-menu-item index="analytics" @click="$router.push('/analytics')">
              <el-icon><data-analysis /></el-icon>
              <span>数据分析</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="bg-gray-950 overflow-auto">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { 
  HomeFilled, 
  DocumentAdd, 
  DocumentCopy, 
  Link, 
  DataAnalysis,
  ArrowDown 
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const auth = useAuthStore();

const handleCommand = (command: string) => {
  if (command === 'logout') {
    auth.logout();
    router.push('/login');
    ElMessage.success('已登出');
  }
};

const handleMenuSelect = () => {
  // 菜单选择处理
};
</script>

<style scoped>
:deep(.el-menu) {
  background-color: transparent;
}

:deep(.el-menu-item) {
  background-color: transparent;
  color: #d1d5db;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(59, 130, 246, 0.1) !important;
  color: #3b82f6;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(59, 130, 246, 0.15) !important;
  color: #3b82f6;
  border-right: 3px solid #3b82f6;
}
</style>
