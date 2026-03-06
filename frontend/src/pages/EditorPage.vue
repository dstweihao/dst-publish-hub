<template>
  <div class="editor-page">
    <div class="editor-header">
      <h1>编辑文章</h1>
      <div class="editor-actions">
        <el-input 
          v-model="article.title" 
          placeholder="输入文章标题"
          class="title-input"
        />
        <el-select v-model="article.theme" placeholder="选择排版样式">
          <el-option label="默认排版 (适合技术)" value="default" />
          <el-option label="GitHub排版 (黑白极简)" value="github" />
          <el-option label="VuePress排版 (清新绿)" value="vuepress" />
          <el-option label="MK-Cute排版 (可爱风)" value="mk-cute" />
          <el-option label="Smart Blue (科技蓝)" value="smart-blue" />
          <el-option label="Cyanisis (唯美青)" value="cyanisis" />
        </el-select>
        <el-button type="success" @click="copyToWechat">
          <el-icon class="el-icon--left"><DocumentCopy /></el-icon>
          复制到公众号
        </el-button>
        <el-button type="primary" @click="saveDraft" :loading="loading">保存草稿</el-button>
      </div>
    </div>
    
    <div class="editor-container">
      <MdEditor 
        v-model="article.markdown" 
        theme="light"
        :preview-theme="article.theme"
        class="md-editor-wrapper"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'
import { useArticleStore } from '@/stores/articleStore'

const articleStore = useArticleStore()
const loading = ref(false)

const article = reactive({
  title: '',
  markdown: '',
  theme: 'default',
  content: ''
})

const saveDraft = async () => {
  if (!article.title) {
    ElMessage.error('请输入文章标题')
    return
  }
  
  loading.value = true
  try {
    await articleStore.createArticle({
      title: article.title,
      markdown: article.markdown,
      content: article.markdown,
      theme: article.theme,
      status: 'draft'
    })
    ElMessage.success('文章已保存为草稿')
  } finally {
    loading.value = false
  }
}

const copyToWechat = async () => {
  // md-editor-v3 uses class 'md-editor-preview' inside its root or wrapper
  const preview = document.querySelector('.md-editor-preview-wrapper') || document.querySelector('.md-editor-preview');
  if (!preview) {
    ElMessage.error('找不到渲染区内容，如果页面空白请先写入 Markdown！');
    return;
  }
  
  try {
    const htmlToCopy = preview.innerHTML;
    // Use HTML Blob so that WeChat formatting is preserved accurately.
    const blobHtml = new Blob([htmlToCopy], { type: 'text/html' });
    const blobText = new Blob([preview.textContent || ''], { type: 'text/plain' });
    
    // @ts-ignore
    const clipboardItem = new window.ClipboardItem({
      'text/html': blobHtml,
      'text/plain': blobText
    });
    
    await navigator.clipboard.write([clipboardItem]);
    ElMessage.success('🎉 样板 HTML 复制成功，快去公众号编辑器愉快地粘贴吧！');
  } catch (err) {
    console.error('Copy failed:', err);
    ElMessage.error('复制失败，请重试或更换高版本现代浏览器');
  }
}
</script>

<style scoped>
.editor-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
}

.editor-header {
  margin-bottom: 1.5rem;
}

.editor-header h1 {
  margin-bottom: 1rem;
  font-size: 28px;
}

.editor-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.title-input {
  min-width: 200px;
}

.editor-container {
  display: flex;
  flex: 1;
  min-height: 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--color-border);
}

.md-editor-wrapper {
  width: 100%;
  height: 100%;
}


</style>
