<template>
  <div class="articles-page p-6">
    <!-- 头部 -->
    <div class="mb-6">
      <div class="flex items-center justify-between mb-4">
        <h1 class="text-3xl font-bold text-white">文章管理</h1>
        <el-button type="primary" @click="$router.push('/editor')">
          <el-icon><document-add /></el-icon>
          <span>新建文章</span>
        </el-button>
      </div>

      <!-- 搜索和筛选 -->
      <div class="flex gap-4 mb-4">
        <el-input
          v-model="searchQuery"
          placeholder="搜索文章标题..."
          clearable
          style="max-width: 300px"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterStatus"
          placeholder="筛选状态"
          clearable
          style="width: 150px"
        >
          <el-option label="全部" value="" />
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
          <el-option label="已计划" value="scheduled" />
        </el-select>

        <el-select
          v-model="sortBy"
          placeholder="排序方式"
          style="width: 150px"
        >
          <el-option label="最新修改" value="updated" />
          <el-option label="最新创建" value="created" />
          <el-option label="阅读量" value="views" />
        </el-select>
      </div>
    </div>

    <!-- 文章列表 -->
    <div v-if="filteredArticles.length > 0" class="space-y-4">
      <div
        v-for="article in filteredArticles"
        :key="article.id"
        class="bg-gray-900 border border-gray-800 rounded-lg p-4 hover:border-blue-500 transition-colors"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1 cursor-pointer" @click="editArticle(article)">
            <h3 class="text-lg font-semibold text-white hover:text-blue-400 transition-colors">
              {{ article.title }}
            </h3>
            <p class="text-gray-400 text-sm mt-1">{{ article.description }}</p>

            <!-- 文章元信息 -->
            <div class="flex items-center gap-4 mt-3 text-sm text-gray-500">
              <span v-if="article.createdAt">
                创建: {{ formatDate(article.createdAt) }}
              </span>
              <span>阅读: {{ article.viewCount || 0 }}</span>
              <el-tag
                :type="article.status === 'published' ? 'success' : 'info'"
                size="small"
              >
                {{ formatStatus(article.status) }}
              </el-tag>
            </div>

            <!-- 发布平台标签 -->
            <div class="flex flex-wrap gap-2 mt-3">
              <el-tag
                v-for="platform in article.platforms"
                :key="platform.name"
                :type="platform.status === 'published' ? 'success' : 'warning'"
                size="small"
              >
                {{ getPlatformLabel(platform.name) }}
              </el-tag>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-2 ml-4">
            <el-tooltip content="编辑">
              <el-button
                type="primary"
                text
                @click="editArticle(article)"
              >
                <el-icon><edit /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip v-if="article.status === 'draft'" content="发布">
              <el-button
                type="success"
                text
                @click="openPublishDialog(article)"
              >
                <el-icon><upload-filled /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip v-if="article.status === 'published'" content="重新发布">
              <el-button
                type="warning"
                text
                @click="openPublishDialog(article)"
              >
                <el-icon><refresh /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip content="预览">
              <el-button
                text
                @click="previewArticle(article)"
              >
                <el-icon><view /></el-icon>
              </el-button>
            </el-tooltip>

            <el-popconfirm
              title="确定要删除此文章吗?"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="deleteArticle(article.id!)"
            >
              <template #reference>
                <el-button type="danger" text>
                  <el-icon><delete /></el-icon>
                </el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredArticles.length"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col items-center justify-center py-12">
      <el-icon class="text-6xl text-gray-600 mb-4"><document-copy /></el-icon>
      <p class="text-gray-400 text-lg">还没有文章，快去创建一篇吧！</p>
      <el-button type="primary" class="mt-4" @click="$router.push('/editor')">
        新建文章
      </el-button>
    </div>

    <!-- 发布对话框 -->
    <el-dialog v-model="publishDialogVisible" title="发布文章" width="600px">
      <div class="space-y-4">
        <el-alert
          title="选择要发布的平台"
          type="info"
          :closable="false"
        />

        <div class="space-y-3 max-h-64 overflow-y-auto">
          <div
            v-for="platform in platforms"
            :key="platform.name"
            class="flex items-center p-3 bg-gray-100 rounded cursor-pointer hover:bg-gray-200"
            @click="togglePublishPlatform(platform.name)"
          >
            <el-checkbox
              :model-value="selectedPublishPlatforms.includes(platform.name)"
              @change="togglePublishPlatform(platform.name)"
            />
            <span class="ml-3 font-medium flex-1">{{ platform.label }}</span>
            <el-tag
              v-if="platform.connected"
              type="success"
              size="small"
            >
              已连接
            </el-tag>
            <el-tag v-else type="warning" size="small">
              未连接
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmPublish"
          :loading="publishingArticle"
        >
          {{ publishingArticle ? '发布中...' : '一键发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="文章预览"
      width="900px"
      class="preview-dialog"
    >
      <div class="article-preview">
        <h1>{{ previewArticle.title }}</h1>
        <div class="meta">
          <span>{{ formatDate(previewArticle.createdAt) }}</span>
          <span>阅读: {{ previewArticle.viewCount || 0 }}</span>
        </div>
        <div class="content" v-html="previewArticle.content" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useArticleStore } from '@/stores/articleStore';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  DocumentAdd, Search, Edit, UploadFilled, Refresh, View,
  Delete, DocumentCopy
} from '@element-plus/icons-vue';
import type { Article, PublishPlatform } from '@/types/article';

const router = useRouter();
const articleStore = useArticleStore();

const searchQuery = ref('');
const filterStatus = ref('');
const sortBy = ref('updated');
const currentPage = ref(1);
const pageSize = ref(10);

const publishDialogVisible = ref(false);
const previewDialogVisible = ref(false);
const publishingArticle = ref(false);
const selectedPublishPlatforms = ref<PublishPlatform['name'][]>([]);
const currentPublishArticle = ref<Article | null>(null);

const previewArticle = ref<Article>({
  title: '',
  content: '',
  status: 'draft',
  platforms: [],
  theme: 'default'
});

const platforms = [
  { name: 'wechat' as const, label: '微信公众号', connected: true },
  { name: 'juejin' as const, label: '稀土掘金', connected: true },
  { name: 'cnblogs' as const, label: '博客园', connected: false },
  { name: 'zhihu' as const, label: '知乎', connected: true },
  { name: 'csdn' as const, label: 'CSDN', connected: true },
  { name: 'toutiao' as const, label: '今日头条', connected: false },
  { name: 'jianshu' as const, label: '简书', connected: true }
];

const filteredArticles = computed(() => {
  let result = articleStore.articles;

  if (searchQuery.value) {
    result = result.filter(a =>
      a.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      a.description?.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  if (filterStatus.value) {
    result = result.filter(a => a.status === filterStatus.value);
  }

  if (sortBy.value === 'updated') {
    result.sort((a, b) => 
      new Date(b.updatedAt || 0).getTime() - new Date(a.updatedAt || 0).getTime()
    );
  } else if (sortBy.value === 'created') {
    result.sort((a, b) =>
      new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime()
    );
  } else if (sortBy.value === 'views') {
    result.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0));
  }

  return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

const formatDate = (date: any) => {
  return new Date(date).toLocaleString('zh-CN');
};

const formatStatus = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    scheduled: '已计划'
  };
  return map[status] || status;
};

const getPlatformLabel = (name: string) => {
  const platform = platforms.find(p => p.name === name);
  return platform?.label || name;
};

const editArticle = (article: Article) => {
  router.push(`/editor/${article.id}`);
};

const openPublishDialog = (article: Article) => {
  currentPublishArticle.value = article;
  selectedPublishPlatforms.value = [];
  publishDialogVisible.value = true;
};

const togglePublishPlatform = (platformName: PublishPlatform['name']) => {
  const index = selectedPublishPlatforms.value.indexOf(platformName);
  if (index > -1) {
    selectedPublishPlatforms.value.splice(index, 1);
  } else {
    selectedPublishPlatforms.value.push(platformName);
  }
};

const confirmPublish = async () => {
  if (!currentPublishArticle.value) return;

  if (selectedPublishPlatforms.value.length === 0) {
    ElMessage.warning('请至少选择一个平台');
    return;
  }

  publishingArticle.value = true;
  try {
    await articleStore.publishArticle(
      currentPublishArticle.value.id!,
      selectedPublishPlatforms.value
    );
    ElMessage.success('文章发布成功！');
    publishDialogVisible.value = false;
  } catch (error) {
    ElMessage.error('发布失败，请稍后重试');
  } finally {
    publishingArticle.value = false;
  }
};

const deleteArticle = async (id: string) => {
  try {
    await articleStore.deleteArticle(id);
    ElMessage.success('删除成功');
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

const openPreviewDialog = (article: Article) => {
  previewArticle.value = article;
  previewDialogVisible.value = true;
};

onMounted(() => {
  articleStore.getArticles();
});
</script>

<style scoped>
:deep(.el-table) {
  background-color: transparent;
  border: none;
}

:deep(.el-table__body tr) {
  background-color: transparent;
  border-bottom: 1px solid #1f2937;
}

:deep(.el-table__body tr:hover > td) {
  background-color: rgba(59, 130, 246, 0.1);
}

.article-preview {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.8;
}

.article-preview h1 {
  font-size: 2.2em;
  font-weight: bold;
  margin: 0.5em 0;
  border-bottom: 2px solid #3b82f6;
  padding-bottom: 0.3em;
}

.article-preview .meta {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 2em;
  padding: 1em;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.article-preview .meta span {
  margin-right: 2em;
}

.article-preview .content {
  margin-top: 2em;
}
</style>
