// 用户认证Pinia store
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { User } from '@/types/article';

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null);
  const token = ref<string | null>(localStorage.getItem('token'));
  const loading = ref(false);
  const error = ref<string | null>(null);

  // 登录
  const login = async (email: string, password: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });
      const data = await response.json();
      if (data.code === 0) {
        token.value = data.data.token;
        user.value = data.data.user;
        localStorage.setItem('token', data.data.token);
        return true;
      } else {
        error.value = data.message;
        return false;
      }
    } catch (err) {
      error.value = '登录失败';
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 注册
  const register = async (username: string, email: string, password: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password })
      });
      const data = await response.json();
      if (data.code === 0) {
        token.value = data.data.token;
        user.value = data.data.user;
        localStorage.setItem('token', data.data.token);
        return true;
      } else {
        error.value = data.message;
        return false;
      }
    } catch (err) {
      error.value = '注册失败';
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 登出
  const logout = () => {
    user.value = null;
    token.value = null;
    localStorage.removeItem('token');
  };

  // 获取当前用户
  const getCurrentUser = async () => {
    if (!token.value) return;
    try {
      const response = await fetch('/api/auth/me', {
        headers: {
          'Authorization': `Bearer ${token.value}`
        }
      });
      const data = await response.json();
      if (data.code === 0) {
        user.value = data.data;
      }
    } catch (err) {
      console.error('获取用户信息失败', err);
    }
  };

  return {
    user,
    token,
    loading,
    error,
    login,
    register,
    logout,
    getCurrentUser
  };
});
