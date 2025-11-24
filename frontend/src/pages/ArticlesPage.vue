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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useArticleStore } from '@/stores/articleStore'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const articleStore = useArticleStore()
const router = useRouter()
const searchText = ref('')

const filteredArticles = computed(() => {
  if (!searchText.value) return articleStore.articles
  return articleStore.articles.filter(a => 
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

const publishArticle = (article: any) => {
  ElMessageBox.confirm(
    '确定要发布该文章吗？',
    '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    articleStore.updateArticle(article.id, { status: 'published' })
    ElMessage.success('文章已发布')
  })
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
</style>
