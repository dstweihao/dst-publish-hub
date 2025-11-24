<template>
  <div class="p-6">
    <!-- 欢迎区 -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-white mb-2">欢迎回来, {{ auth.user?.username }}! 👋</h1>
      <p class="text-gray-400">一键发布您的优质内容到多个平台</p>
    </div>

    <!-- 快速统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
      <el-card class="bg-gray-900 border-gray-800 hover:shadow-lg hover:shadow-blue-500/20 transition-shadow">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-gray-400">总文章数</span>
            <el-icon class="text-blue-500"><document-copy /></el-icon>
          </div>
        </template>
        <div class="text-3xl font-bold text-white">{{ stats.totalArticles }}</div>
        <p class="text-sm text-gray-500 mt-2">较上周 +2</p>
      </el-card>

      <el-card class="bg-gray-900 border-gray-800 hover:shadow-lg hover:shadow-green-500/20 transition-shadow">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-gray-400">已发布</span>
            <el-icon class="text-green-500"><success-filled /></el-icon>
          </div>
        </template>
        <div class="text-3xl font-bold text-white">{{ stats.publishedArticles }}</div>
        <p class="text-sm text-gray-500 mt-2">总阅读量 {{ stats.totalViews }}</p>
      </el-card>

      <el-card class="bg-gray-900 border-gray-800 hover:shadow-lg hover:shadow-purple-500/20 transition-shadow">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-gray-400">草稿</span>
            <el-icon class="text-purple-500"><document /></el-icon>
          </div>
        </template>
        <div class="text-3xl font-bold text-white">{{ stats.draftArticles }}</div>
        <p class="text-sm text-gray-500 mt-2">等待发布</p>
      </el-card>

      <el-card class="bg-gray-900 border-gray-800 hover:shadow-lg hover:shadow-orange-500/20 transition-shadow">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-gray-400">平台连接</span>
            <el-icon class="text-orange-500"><link /></el-icon>
          </div>
        </template>
        <div class="text-3xl font-bold text-white">{{ stats.connectedPlatforms }}/7</div>
        <p class="text-sm text-gray-500 mt-2">配置更多平台</p>
      </el-card>
    </div>

    <!-- 最近文章 -->
    <el-card class="bg-gray-900 border-gray-800 mb-8">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="text-lg font-semibold text-white">最近发布</span>
          <el-button type="primary" @click="$router.push('/editor')">新建文章</el-button>
        </div>
      </template>
      <el-table :data="recentArticles" class="w-full" stripe>
        <el-table-column prop="title" label="标题" width="300" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'info'">
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="最后修改" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editArticle(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="deleteArticle(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useArticleStore } from '@/stores/articleStore';
import { useRouter } from 'vue-router';
import { DocumentCopy, SuccessFilled, Document, Link } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const auth = useAuthStore();
const articleStore = useArticleStore();

const stats = ref({
  totalArticles: 12,
  publishedArticles: 8,
  draftArticles: 4,
  totalViews: 2450,
  connectedPlatforms: 5
});

const recentArticles = ref([
  {
    id: '1',
    title: 'Vue 3 最佳实践',
    status: 'published',
    updatedAt: '2025-01-10 14:30'
  },
  {
    id: '2',
    title: 'TypeScript 高级技巧',
    status: 'draft',
    updatedAt: '2025-01-09 10:15'
  },
  {
    id: '3',
    title: 'Web 性能优化指南',
    status: 'published',
    updatedAt: '2025-01-08 16:45'
  }
]);

onMounted(() => {
  auth.getCurrentUser();
});

const editArticle = (article: any) => {
  router.push(`/editor/${article.id}`);
};

const deleteArticle = async (article: any) => {
  try {
    await ElMessageBox.confirm('确定删除此文章吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    await articleStore.deleteArticle(article.id);
    ElMessage.success('删除成功');
  } catch {
    // 用户取消删除
  }
};
</script>

<style scoped>
:deep(.el-table) {
  background-color: transparent;
  border: none;
}

:deep(.el-table__header) {
  background-color: #111827;
}

:deep(.el-table__body tr) {
  background-color: transparent;
  border-bottom: 1px solid #1f2937;
}

:deep(.el-table__body tr:hover > td) {
  background-color: rgba(59, 130, 246, 0.1);
}
</style>
