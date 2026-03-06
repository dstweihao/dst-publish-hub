import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Article } from '@/types'
import api from '@/utils/api'

export const useArticleStore = defineStore('article', () => {
  const articles = ref<Article[]>([])
  const currentArticle = ref<Article | null>(null)
  const loading = ref(false)
  const connectedPlatformsCount = ref(0)

  const articlesCount = computed(() => articles.value.length)
  const draftArticles = computed(() => articles.value.filter(a => a.status === 'draft'))
  const publishedArticles = computed(() => articles.value.filter(a => a.status === 'published'))

  const fetchArticles = async () => {
    loading.value = true
    try {
      const response = await api.get('/articles')
      if (response.data && Array.isArray(response.data.content)) {
        articles.value = response.data.content
      } else if (Array.isArray(response.data)) {
        articles.value = response.data
      } else {
        articles.value = []
      }
    } catch (error) {
      console.error('Failed to fetch articles:', error)
      articles.value = []
    } finally {
      loading.value = false
    }
  }

  const fetchArticle = async (id: string) => {
    loading.value = true
    try {
      const response = await api.get(`/articles/${id}`)
      currentArticle.value = response.data
    } finally {
      loading.value = false
    }
  }

  const createArticle = async (article: Omit<Article, 'id' | 'createdAt' | 'updatedAt' | 'userId'>) => {
    const response = await api.post('/articles', article)
    articles.value.push(response.data)
    return response.data
  }

  const updateArticle = async (id: string, article: Partial<Article>) => {
    const response = await api.put(`/articles/${id}`, article)
    const index = articles.value.findIndex(a => a.id === id)
    if (index > -1) {
      articles.value[index] = response.data
    }
    return response.data
  }

  const deleteArticle = async (id: string) => {
    await api.delete(`/articles/${id}`)
    articles.value = articles.value.filter(a => a.id !== id)
  }

  const publishToPlatforms = async (articleId: number | string, platformNames: string[]) => {
    loading.value = true
    try {
      // Endpoint is /api/publish/article/{articleId}
      // Note: baseURL is /api, so we use /publish/article/
      const response = await api.post(`/publish/article/${articleId}`, {
        platforms: platformNames
      })
      return response.data
    } finally {
      loading.value = false
    }
  }

  const fetchPlatformAccountsCount = async () => {
    try {
      const response = await api.get('/platforms')
      const platforms = response.data || []
      connectedPlatformsCount.value = platforms.length
    } catch (err) {
      console.error('Failed to fetch platforms count', err)
    }
  }

  return {
    articles,
    currentArticle,
    loading,
    connectedPlatformsCount,
    articlesCount,
    draftArticles,
    publishedArticles,
    fetchArticles,
    fetchArticle,
    createArticle,
    updateArticle,
    deleteArticle,
    publishToPlatforms,
    fetchPlatformAccountsCount
  }
})
