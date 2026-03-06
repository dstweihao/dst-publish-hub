<template>
  <div class="login-container">
    <div class="login-box">
      <h1>文章发布平台</h1>
      <p class="subtitle">登录你的账户</p>
      
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
      </el-form>
      
      <div class="login-footer">
        <p>没有账户？<router-link to="/register">注册新账户</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({
  email: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.email || !form.password) {
    ElMessage.error('请输入邮箱和密码')
    return
  }
  
  loading.value = true
  try {
    await authStore.login(form.email, form.password)
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-box {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 3rem;
  border-radius: 16px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 420px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: transform 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
}

.login-box h1 {
  text-align: center;
  margin-bottom: 0.5rem;
  color: #1f2937;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.subtitle {
  text-align: center;
  color: #4b5563;
  margin-bottom: 2.5rem;
  font-size: 1.1rem;
}

.el-button {
  width: 100%;
  margin-top: 1rem;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 20px rgba(118, 75, 162, 0.4);
}

.login-footer {
  text-align: center;
  margin-top: 2rem;
  color: #4b5563;
  font-size: 0.95rem;
}

.login-footer a {
  color: #764ba2;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.3s;
}

.login-footer a:hover {
  color: #e73c7e;
  text-decoration: underline;
}
</style>
