import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Article } from '@/types'
import api from '@/utils/api'

export const useArticleStore = defineStore('article', () => {
  const articles = ref<Article[]>([])
  const currentArticle = ref<Article | null>(null)
  const loading = ref(false)

  const articlesCount = computed(() => articles.value.length)
  const draftArticles = computed(() => articles.value.filter(a => a.status === 'draft'))
  const publishedArticles = computed(() => articles.value.filter(a => a.status === 'published'))

  const fetchArticles = async () => {
    loading.value = true
    try {
      const response = await api.get('/articles')
      articles.value = response.data
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

  return {
    articles,
    currentArticle,
    loading,
    articlesCount,
    draftArticles,
    publishedArticles,
    fetchArticles,
    fetchArticle,
    createArticle,
    updateArticle,
    deleteArticle
  }
})
