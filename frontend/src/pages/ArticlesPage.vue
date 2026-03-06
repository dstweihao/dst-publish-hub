<template>
  <div class="articles-page">
    <h1>文章管理</h1>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="$router.push('/editor')">新建文章</el-button>
          <el-input-group class="search-group">
            <el-input v-model="searchText" placeholder="搜索文章..." />
            <el-button @click="handleSearch">搜索</el-button>
          </el-input-group>
        </div>
      </template>
      
      <el-table :data="filteredArticles" stripe v-loading="articleStore.loading">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="theme" label="主题" width="100" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editArticle(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="publishArticle(row)">发布</el-button>
            <el-button size="small" type="danger" @click="deleteArticle(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布对话框 -->
    <el-dialog v-model="publishDialogVisible" title="一键群发至各大平台" width="500px">
      <div class="publish-dialog-content" v-loading="loadingPlatforms">
        <p class="dialog-tip">请选择您要分发的目标平台（仅显示已连接的平台）：</p>
        
        <el-checkbox-group v-if="connectedPlatforms.length > 0" v-model="selectedPlatforms">
          <div class="platform-checkbox-list">
            <el-checkbox v-for="p in connectedPlatforms" :key="p.name" :label="p.name">
              <div class="platform-checkbox-label">
                <el-icon :style="{ color: p.color }" class="platform-icon-mini">
                  <component :is="p.icon" />
                </el-icon>
                <span>{{ p.name }}</span>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
        
        <div v-else class="no-platforms">
          <el-empty description="暂无已连接的平台">
            <el-button type="primary" @click="$router.push('/platforms')">去配置平台</el-button>
          </el-empty>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button type="success" :disabled="selectedPlatforms.length === 0" 
            :loading="publishing" @click="confirmPublish">
            同步发布
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 发布结果反馈 -->
    <el-dialog v-model="resultDialogVisible" title="发布结果反馈" width="600px">
      <el-table :data="publishResults">
        <el-table-column prop="platform" label="平台" width="160">
          <template #default="{ row }">
            <div class="platform-result-info">
              <el-icon :style="{ color: row.color }" class="platform-icon-mini">
                <component :is="row.icon" />
              </el-icon>
              <span>{{ row.platform }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="success" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.success ? 'success' : 'danger'">
              {{ row.success ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedUrl" label="链接/错误信息">
          <template #default="{ row }">
            <el-link v-if="row.success" :href="row.publishedUrl" target="_blank" type="primary">
              查看文章
            </el-link>
            <span v-else class="error-msg">{{ row.errorMessage }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useArticleStore } from '@/stores/articleStore'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import { SUPPORTED_PLATFORMS } from '@/config/platforms'

const articleStore = useArticleStore()
const router = useRouter()
const searchText = ref('')

const filteredArticles = computed(() => {
  if (!searchText.value) return articleStore.articles
  return articleStore.articles.filter((a: any) => 
    a.title.includes(searchText.value)
  )
})

onMounted(() => {
  articleStore.fetchArticles()
})

const handleSearch = () => {
  // 搜索逻辑已在 computed 中实现
}

const editArticle = (article: any) => {
  articleStore.currentArticle = article
  router.push('/editor')
}

const publishDialogVisible = ref(false)
const resultDialogVisible = ref(false)
const loadingPlatforms = ref(false)
const publishing = ref(false)
const connectedPlatforms = ref<any[]>([])
const selectedPlatforms = ref<string[]>([])
const currentPublishArticleId = ref<any>(null)
const publishResults = ref<any[]>([])

const publishArticle = async (article: any) => {
  currentPublishArticleId.value = article.id
  publishDialogVisible.value = true
  selectedPlatforms.value = []
  
  loadingPlatforms.value = true
  try {
    const res = await api.get('/platforms')
    const connectedData = res.data || []
    
    // 匹配已连接的平台并丰富元数据
    connectedPlatforms.value = SUPPORTED_PLATFORMS.filter(p => 
      connectedData.some((c: any) => c.platform === p.name)
    )
  } catch (err) {
    console.error('Failed to fetch connected platforms', err)
  } finally {
    loadingPlatforms.value = false
  }
}

const confirmPublish = async () => {
  if (selectedPlatforms.value.length === 0) return
  
  publishing.value = true
  try {
    const res = await articleStore.publishToPlatforms(currentPublishArticleId.value, selectedPlatforms.value)
    
    // Process results from backend
    const resultsMap = res || {}
    publishResults.value = Object.keys(resultsMap).map(key => {
      const config = SUPPORTED_PLATFORMS.find(p => p.name === key);
      return {
        platform: key,
        icon: config?.icon || 'document',
        color: config?.color || '#909399',
        ...resultsMap[key]
      }
    })
    
    publishDialogVisible.value = false
    resultDialogVisible.value = true
    
    // Refresh list to update status if needed
    articleStore.fetchArticles()
  } catch (err) {
    console.error('Publishing failed', err)
  } finally {
    publishing.value = false
  }
}

const deleteArticle = (article: any) => {
  ElMessageBox.confirm(
    '确定要删除该文章吗？',
    '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    articleStore.deleteArticle(article.id)
    ElMessage.success('文章已删除')
  })
}
</script>

<style scoped>
.articles-page h1 {
  margin-bottom: 1.5rem;
  font-size: 28px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.search-group {
  width: 300px;
}

.publish-dialog-content {
  padding: 10px 0;
}

.dialog-tip {
  margin-bottom: 20px;
  color: var(--el-text-color-secondary);
}

.platform-checkbox-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.platform-checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.platform-icon-mini {
  font-size: 18px;
}

.platform-result-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.error-msg {
  color: var(--el-color-danger);
  font-size: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
