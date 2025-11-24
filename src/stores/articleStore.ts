// 文章管理Pinia store
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { Article, PublishPlatform } from '@/types/article';

export const useArticleStore = defineStore('article', () => {
  const articles = ref<Article[]>([]);
  const currentArticle = ref<Article | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // 获取所有文章
  const getArticles = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('/api/articles', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      const data = await response.json();
      if (data.code === 0) {
        articles.value = data.data;
      } else {
        error.value = data.message;
      }
    } catch (err) {
      error.value = '获取文章列表失败';
    } finally {
      loading.value = false;
    }
  };

  // 创建新文章
  const createArticle = async (article: Article) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('/api/articles', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(article)
      });
      const data = await response.json();
      if (data.code === 0) {
        articles.value.push(data.data);
        currentArticle.value = data.data;
        return data.data;
      } else {
        error.value = data.message;
      }
    } catch (err) {
      error.value = '创建文章失败';
    } finally {
      loading.value = false;
    }
  };

  // 更新文章
  const updateArticle = async (id: string, article: Partial<Article>) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`/api/articles/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(article)
      });
      const data = await response.json();
      if (data.code === 0) {
        const index = articles.value.findIndex(a => a.id === id);
        if (index > -1) {
          articles.value[index] = data.data;
        }
        if (currentArticle.value?.id === id) {
          currentArticle.value = data.data;
        }
        return data.data;
      } else {
        error.value = data.message;
      }
    } catch (err) {
      error.value = '更新文章失败';
    } finally {
      loading.value = false;
    }
  };

  // 删除文章
  const deleteArticle = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`/api/articles/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      const data = await response.json();
      if (data.code === 0) {
        articles.value = articles.value.filter(a => a.id !== id);
        if (currentArticle.value?.id === id) {
          currentArticle.value = null;
        }
      } else {
        error.value = data.message;
      }
    } catch (err) {
      error.value = '删除文章失败';
    } finally {
      loading.value = false;
    }
  };

  // 发布文章到多个平台
  const publishArticle = async (id: string, platforms: PublishPlatform['name'][]) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`/api/articles/${id}/publish`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({ platforms })
      });
      const data = await response.json();
      if (data.code === 0) {
        const article = articles.value.find(a => a.id === id);
        if (article) {
          article.status = 'published';
          article.platforms = data.data.platforms;
        }
        return data.data;
      } else {
        error.value = data.message;
      }
    } catch (err) {
      error.value = '发布失败';
    } finally {
      loading.value = false;
    }
  };

  return {
    articles,
    currentArticle,
    loading,
    error,
    getArticles,
    createArticle,
    updateArticle,
    deleteArticle,
    publishArticle
  };
});
