<template>
  <div class="editor-page h-full flex flex-col bg-gray-950">
    <!-- 头部 -->
    <div class="bg-gray-900 border-b border-gray-800 p-4">
      <div class="flex items-center justify-between">
        <div class="flex-1">
          <el-input
            v-model="article.title"
            placeholder="输入文章标题..."
            size="large"
            class="title-input"
            @input="autoSave"
          >
            <template #prefix>
              <el-icon><document-add /></el-icon>
            </template>
          </el-input>
        </div>

        <div class="flex items-center gap-3 ml-4">
          <el-button @click="saveDraft" type="default">保存草稿</el-button>
          <el-button @click="publishDialog = true" type="primary">发布</el-button>
        </div>
      </div>

      <!-- 文章元数据 -->
      <div class="mt-4 flex items-center gap-4">
        <el-input
          v-model="article.description"
          placeholder="文章描述..."
          size="small"
          style="flex: 1"
        />
        
        <el-select 
          v-model="article.theme" 
          placeholder="选择主题"
          size="small"
          style="width: 150px"
        >
          <el-option 
            v-for="theme in themeStore.themes"
            :key="theme.id"
            :label="theme.name"
            :value="theme.id"
          />
        </el-select>

        <el-button-group>
          <el-button 
            @click="viewMode = 'editor'"
            :type="viewMode === 'editor' ? 'primary' : 'default'"
            size="small"
          >
            编辑
          </el-button>
          <el-button 
            @click="viewMode = 'theme'"
            :type="viewMode === 'theme' ? 'primary' : 'default'"
            size="small"
          >
            主题
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 内容区 -->
    <div class="flex-1 overflow-hidden">
      <div v-if="viewMode === 'editor'" class="h-full">
        <MarkdownEditor v-model="article.content" />
      </div>
      <div v-else class="h-full overflow-auto p-6">
        <ThemeSelector />
      </div>
    </div>

    <!-- 发布对话框 -->
    <el-dialog v-model="publishDialog" title="发布文章" width="600px">
      <div class="space-y-4">
        <el-alert title="请选择要发布的平台" type="info" :closable="false" />
        
        <div class="space-y-3">
          <div 
            v-for="platform in platforms"
            :key="platform.name"
            class="flex items-center p-3 bg-gray-100 rounded cursor-pointer hover:bg-gray-200"
            @click="togglePlatform(platform.name)"
          >
            <el-checkbox 
              :model-value="selectedPlatforms.includes(platform.name)"
              @change="togglePlatform(platform.name)"
            />
            <span class="ml-3 font-medium">{{ platform.label }}</span>
            <el-tag 
              v-if="platform.connected"
              type="success"
              size="small"
              class="ml-auto"
            >
              已连接
            </el-tag>
            <el-tag 
              v-else
              type="warning"
              size="small"
              class="ml-auto"
            >
              未连接
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="publishDialog = false">取消</el-button>
        <el-button 
          @click="handlePublish"
          type="primary"
          :loading="publishing"
        >
          {{ publishing ? '发布中...' : '一键发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useArticleStore } from '@/stores/articleStore';
import { useThemeStore } from '@/stores/themeStore';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import MarkdownEditor from '@/components/MarkdownEditor.vue';
import ThemeSelector from '@/components/ThemeSelector.vue';
import { DocumentAdd } from '@element-plus/icons-vue';
import type { Article, PublishPlatform } from '@/types/article';

const router = useRouter();
const route = useRoute();
const articleStore = useArticleStore();
const themeStore = useThemeStore();

const viewMode = ref<'editor' | 'theme'>('editor');
const publishDialog = ref(false);
const publishing = ref(false);
const selectedPlatforms = ref<PublishPlatform['name'][]>([]);
const autoSaveTimer = ref<NodeJS.Timeout>();

const article = ref<Article>({
  title: '',
  content: '',
  description: '',
  theme: 'default',
  platforms: [],
  status: 'draft',
  tags: []
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

const autoSave = () => {
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value);
  }
  autoSaveTimer.value = setTimeout(() => {
    saveDraft();
  }, 3000);
};

const saveDraft = async () => {
  try {
    const result = await articleStore.createArticle(article.value);
    if (result) {
      ElMessage.success('草稿已保存');
    }
  } catch (error) {
    ElMessage.error('保存失败');
  }
};

const togglePlatform = (platformName: PublishPlatform['name']) => {
  const index = selectedPlatforms.value.indexOf(platformName);
  if (index > -1) {
    selectedPlatforms.value.splice(index, 1);
  } else {
    selectedPlatforms.value.push(platformName);
  }
};

const handlePublish = async () => {
  if (selectedPlatforms.value.length === 0) {
    ElMessage.warning('请至少选择一个平台');
    return;
  }

  if (!article.value.title.trim()) {
    ElMessage.warning('请输入文章标题');
    return;
  }

  if (!article.value.content.trim()) {
    ElMessage.warning('请输入文章内容');
    return;
  }

  publishing.value = true;
  try {
    await articleStore.publishArticle(article.value.id!, selectedPlatforms.value);
    ElMessage.success('文章发布成功！');
    publishDialog.value = false;
    router.push('/articles');
  } catch (error) {
    ElMessage.error('发布失败，请稍后重试');
  } finally {
    publishing.value = false;
  }
};

onMounted(() => {
  if (route.params.id) {
    // 加载现有文章
  }
});
</script>

<style scoped>
:deep(.title-input .el-input__wrapper) {
  background-color: transparent;
  border: none;
}

:deep(.title-input input) {
  font-size: 1.5em;
  font-weight: bold;
  color: white;
}

:deep(.title-input input::placeholder) {
  color: #6b7280;
}
</style>
