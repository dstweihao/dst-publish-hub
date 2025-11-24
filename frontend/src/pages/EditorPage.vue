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
        <el-select v-model="article.theme" placeholder="选择主题">
          <el-option label="默认主题" value="default" />
          <el-option label="深色专业" value="dark" />
          <el-option label="学术主题" value="academic" />
          <el-option label="炫彩渐变" value="vibrant" />
        </el-select>
        <el-button type="primary" @click="saveDraft" :loading="loading">保存草稿</el-button>
      </div>
    </div>
    
    <div class="editor-container">
      <div class="editor-pane">
        <div class="editor-toolbar">
          <el-button-group>
            <el-button size="small" @click="insertMarkdown('# ')">H1</el-button>
            <el-button size="small" @click="insertMarkdown('## ')">H2</el-button>
            <el-button size="small" @click="insertMarkdown('**加粗**')">加粗</el-button>
            <el-button size="small" @click="insertMarkdown('*斜体*')">斜体</el-button>
            <el-button size="small" @click="insertMarkdown('- ')">列表</el-button>
            <el-button size="small" @click="insertMarkdown('```\n\n```')">代码块</el-button>
          </el-button-group>
        </div>
        <textarea 
          v-model="article.markdown"
          placeholder="在此输入Markdown内容..."
          class="markdown-textarea"
        />
      </div>
      
      <div class="preview-pane">
        <div class="preview-header">预览</div>
        <div class="preview-content" v-html="renderedHtml" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { marked } from 'marked'
import { ElMessage } from 'element-plus'
import { useArticleStore } from '@/stores/articleStore'

const articleStore = useArticleStore()
const loading = ref(false)

const article = reactive({
  title: '',
  markdown: '',
  theme: 'default',
  content: ''
})

const renderedHtml = computed(() => {
  try {
    return marked(article.markdown)
  } catch (e) {
    return '<p>Markdown 解析错误</p>'
  }
})

const insertMarkdown = (syntax: string) => {
  const textarea = document.querySelector('.markdown-textarea') as HTMLTextAreaElement
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selected = article.markdown.substring(start, end)
  
  article.markdown = 
    article.markdown.substring(0, start) +
    syntax +
    selected +
    article.markdown.substring(end)
}

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
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  flex: 1;
  min-height: 0;
}

.editor-pane, .preview-pane {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  padding: 0.75rem;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-background);
}

.markdown-textarea {
  flex: 1;
  padding: 1rem;
  border: none;
  background: var(--color-background);
  color: var(--color-text-primary);
  resize: none;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.preview-header {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-background);
  font-weight: bold;
}

.preview-content {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3) {
  margin: 1rem 0 0.5rem 0;
  font-weight: bold;
}

.preview-content :deep(h1) { font-size: 32px; }
.preview-content :deep(h2) { font-size: 28px; }
.preview-content :deep(h3) { font-size: 24px; }

.preview-content :deep(p) {
  margin: 0.5rem 0;
  line-height: 1.6;
}

.preview-content :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.preview-content :deep(pre) {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  margin: 0.5rem 0;
}

@media (max-width: 1024px) {
  .editor-container {
    grid-template-columns: 1fr;
  }
}
</style>
