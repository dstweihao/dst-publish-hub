<template>
  <div class="dashboard">
    <h1>{{ $t('dashboard.title') }}</h1>
    
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>{{ $t('dashboard.totalArticles') }}</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.articlesCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>{{ $t('dashboard.drafts') }}</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.draftArticles.length }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>{{ $t('dashboard.published') }}</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.publishedArticles.length }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>{{ $t('dashboard.connectedPlatforms') }}</span>
            </div>
          </template>
          <div class="stat-number">{{ articleStore.connectedPlatformsCount }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-top: 2rem;">
      <template #header>
        <div class="card-header">
          <span>{{ $t('dashboard.quickActions') }}</span>
        </div>
      </template>
      <el-space wrap>
        <el-button type="primary" @click="$router.push('/editor')">{{ $t('dashboard.newArticle') }}</el-button>
        <el-button @click="$router.push('/articles')">{{ $t('dashboard.manageArticles') }}</el-button>
        <el-button @click="$router.push('/platforms')">{{ $t('dashboard.configPlatforms') }}</el-button>
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
  articleStore.fetchPlatformAccountsCount()
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
