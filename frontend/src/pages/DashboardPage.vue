<template>
  <div class="dashboard">
    <h1>仪表板</h1>
    
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>文章总数</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.articlesCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>草稿数</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.draftArticles.length }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已发布</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.publishedArticles.length }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>连接平台</span>
            </div>
          </template>
          <div class="stat-number">16</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-top: 2rem;">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <el-space wrap>
        <el-button type="primary" @click="$router.push('/editor')">新建文章</el-button>
        <el-button @click="$router.push('/articles')">管理文章</el-button>
        <el-button @click="$router.push('/platforms')">配置平台</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useArticleStore } from '@/stores/articleStore'

const articleStore = useArticleStore()

onMounted(() => {
  articleStore.fetchArticles()
})
</script>

<style scoped>
.dashboard h1 {
  margin-bottom: 2rem;
  font-size: 28px;
  color: var(--color-text-primary);
}

.stat-card {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: var(--color-primary);
  margin: 1rem 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
