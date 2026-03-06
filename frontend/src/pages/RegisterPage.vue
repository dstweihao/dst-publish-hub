<template>
  <div class="register-container">
    <div class="register-box">
      <h1>创建新账户</h1>
      <p class="subtitle">加入文章发布平台</p>
      
      <el-form :model="form" @submit.prevent="handleRegister">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" type="email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
      </el-form>
      
      <div class="register-footer">
        <p>已有账户？<router-link to="/login">登录</router-link></p>
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
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (!form.username || !form.email || !form.password) {
    ElMessage.error('请填写所有字段')
    return
  }
  
  if (form.password !== form.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    await authStore.register(form.username, form.email, form.password)
    ElMessage.success('注册成功，请登录')
  } catch (error) {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(-45deg, #16a085, #f4d03f, #3498db, #9b59b6);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.register-box {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 3rem;
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 420px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  margin: 2rem 0;
}

html.dark .register-box {
  background: rgba(20, 20, 20, 0.75);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.6);
}

.register-box:hover {
  transform: translateY(-5px);
}

.register-box h1 {
  text-align: center;
  margin-bottom: 0.5rem;
  color: #1f2937;
  font-weight: 800;
  letter-spacing: -0.5px;
}

html.dark .register-box h1 {
  color: #ffffff;
}

.subtitle {
  text-align: center;
  color: #4b5563;
  margin-bottom: 2.5rem;
  font-size: 1.1rem;
}

html.dark .subtitle {
  color: #a3a6ad;
}

.el-button {
  width: 100%;
  margin-top: 1rem;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  font-weight: 600;
  background: linear-gradient(135deg, #16a085 0%, #3498db 100%);
  border: none;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 20px rgba(22, 160, 133, 0.4);
}

.register-footer {
  text-align: center;
  margin-top: 2rem;
  color: #4b5563;
  font-size: 0.95rem;
}

html.dark .register-footer {
  color: #a3a6ad;
}

.register-footer a {
  color: #3498db;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.3s;
}

.register-footer a:hover {
  color: #9b59b6;
  text-decoration: underline;
}
</style>
