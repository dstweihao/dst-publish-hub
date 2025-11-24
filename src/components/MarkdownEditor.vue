<template>
  <div class="markdown-editor h-full flex flex-col bg-gray-900">
    <!-- 工具栏 -->
    <div class="editor-toolbar bg-gray-800 border-b border-gray-700 p-3 flex flex-wrap gap-2">
      <el-button-group>
        <el-tooltip content="加粗 (Ctrl+B)">
          <el-button @click="insertMarkdown('**', '**', '加粗文本')" size="small" text>
            <el-icon><bold /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="斜体 (Ctrl+I)">
          <el-button @click="insertMarkdown('*', '*', '斜体文本')" size="small" text>
            <el-icon><italic /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="删除线">
          <el-button @click="insertMarkdown('~~', '~~', '删除线')" size="small" text>
            <el-icon><delete /></el-icon>
          </el-button>
        </el-tooltip>
      </el-button-group>

      <el-divider direction="vertical" />

      <el-button-group>
        <el-tooltip content="H1标题">
          <el-button @click="insertMarkdown('# ', '', '标题')" size="small" text>H1</el-button>
        </el-tooltip>
        <el-tooltip content="H2标题">
          <el-button @click="insertMarkdown('## ', '', '标题')" size="small" text>H2</el-button>
        </el-tooltip>
        <el-tooltip content="H3标题">
          <el-button @click="insertMarkdown('### ', '', '标题')" size="small" text>H3</el-button>
        </el-tooltip>
      </el-button-group>

      <el-divider direction="vertical" />

      <el-button-group>
        <el-tooltip content="无序列表">
          <el-button @click="insertMarkdown('- ', '', '列表项')" size="small" text>
            <el-icon><unordered-list /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="有序列表">
          <el-button @click="insertMarkdown('1. ', '', '列表项')" size="small" text>
            <el-icon><ordered-list /></el-icon>
          </el-button>
        </el-tooltip>
      </el-button-group>

      <el-divider direction="vertical" />

      <el-tooltip content="代码块">
        <el-button @click="insertCodeBlock" size="small" text>
          <el-icon><code-copy /></el-icon>
        </el-button>
      </el-tooltip>

      <el-tooltip content="引用">
        <el-button @click="insertMarkdown('> ', '', '引用文本')" size="small" text>
          <el-icon><document-copy /></el-icon>
        </el-button>
      </el-tooltip>

      <el-tooltip content="链接">
        <el-button @click="insertLink" size="small" text>
          <el-icon><link /></el-icon>
        </el-button>
      </el-tooltip>

      <el-tooltip content="图片">
        <el-button @click="insertImage" size="small" text>
          <el-icon><picture /></el-icon>
        </el-button>
      </el-tooltip>

      <el-divider direction="vertical" />

      <el-button @click="togglePreview" type="primary" size="small">
        {{ showPreview ? '隐藏预览' : '显示预览' }}
      </el-button>
    </div>

    <!-- 编辑区和预览区 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 编辑器 -->
      <div :class="['flex-1', 'overflow-hidden', showPreview ? 'border-r border-gray-700' : '']">
        <textarea
          v-model="content"
          class="w-full h-full bg-gray-950 text-gray-100 p-4 font-mono text-sm resize-none focus:outline-none"
          placeholder="开始编写您的Markdown文章..."
          @keydown="handleKeydown"
        />
      </div>

      <!-- 预览区 -->
      <div v-if="showPreview" class="flex-1 overflow-auto bg-white p-6">
        <div class="article-content" v-html="renderedHtml" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Bold, Italic, Delete, UnorderedList, OrderedList, 
  CodeCopy, DocumentCopy, Link, Picture 
} from '@element-plus/icons-vue';
import marked from 'marked';

const props = defineProps<{
  modelValue: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string];
}>();

const content = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

const showPreview = ref(true);
const editorRef = ref<HTMLTextAreaElement>();

const renderedHtml = computed(() => {
  return marked(content.value);
});

const insertMarkdown = (before: string, after: string, placeholder: string) => {
  if (!editorRef.value) return;
  
  const textarea = editorRef.value;
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const text = content.value;
  const selected = text.substring(start, end);
  
  const newText = 
    text.substring(0, start) + 
    before + 
    (selected || placeholder) + 
    after + 
    text.substring(end);
  
  content.value = newText;
  
  setTimeout(() => {
    textarea.focus();
    const newCursorPos = start + before.length + (selected ? selected.length : placeholder.length);
    textarea.setSelectionRange(newCursorPos, newCursorPos);
  }, 0);
};

const insertCodeBlock = () => {
  const codeBlock = '\n```javascript\n// 您的代码在这里\n```\n';
  const start = editorRef.value?.selectionStart || content.value.length;
  const newContent = content.value.substring(0, start) + codeBlock + content.value.substring(start);
  content.value = newContent;
};

const insertLink = async () => {
  const url = await ElMessageBox.prompt('请输入链接地址', '插入链接', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^https?:\/\/.+/,
    inputErrorMessage: '请输入有效的URL'
  }).catch(() => null);

  if (url) {
    insertMarkdown('[链接文本](', `"${url}")`, '链接文本');
  }
};

const insertImage = async () => {
  const url = await ElMessageBox.prompt('请输入图片URL', '插入图片', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).catch(() => null);

  if (url) {
    insertMarkdown('![', `](${url})`, '图片描述');
  }
};

const togglePreview = () => {
  showPreview.value = !showPreview.value;
};

const handleKeydown = (e: KeyboardEvent) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 'b') {
    e.preventDefault();
    insertMarkdown('**', '**', '加粗文本');
  } else if ((e.ctrlKey || e.metaKey) && e.key === 'i') {
    e.preventDefault();
    insertMarkdown('*', '*', '斜体文本');
  }
};
</script>

<style scoped>
.markdown-editor {
  font-family: 'Monaco', 'Courier New', monospace;
}

.article-content {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.8;
  color: #333;
}

.article-content h1 {
  font-size: 2.2em;
  font-weight: bold;
  margin: 1.5em 0 0.5em 0;
  border-bottom: 2px solid #3b82f6;
  padding-bottom: 0.3em;
}

.article-content h2 {
  font-size: 1.8em;
  font-weight: bold;
  margin: 1.3em 0 0.4em 0;
  color: #3b82f6;
}

.article-content h3 {
  font-size: 1.4em;
  margin: 1em 0 0.3em 0;
}

.article-content p {
  margin: 1em 0;
}

.article-content code {
  background-color: #f5f5f5;
  border-radius: 3px;
  padding: 2px 6px;
  font-family: 'Monaco', 'Courier New', monospace;
}

.article-content pre {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 1em;
  border-radius: 5px;
  overflow-x: auto;
  margin: 1em 0;
}

.article-content blockquote {
  border-left: 4px solid #3b82f6;
  padding-left: 1em;
  margin-left: 0;
  color: #666;
}

.article-content ul,
.article-content ol {
  margin-left: 2em;
  margin-bottom: 1em;
}

.article-content li {
  margin-bottom: 0.5em;
}

.article-content a {
  color: #3b82f6;
  text-decoration: underline;
}

:deep(.el-button-group) {
  display: inline-flex;
}
</style>
