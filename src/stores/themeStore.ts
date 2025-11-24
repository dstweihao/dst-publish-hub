// 主题管理Store
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export interface Theme {
  name: string;
  id: string;
  cssContent: string;
  preview: string;
}

export const useThemeStore = defineStore('theme', () => {
  const themes = ref<Theme[]>([
    {
      name: '默认主题',
      id: 'default',
      cssContent: `.article-content { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif; line-height: 1.8; color: #333; } .article-content h1 { font-size: 2.2em; font-weight: bold; margin: 1.5em 0 0.5em 0; border-bottom: 2px solid #3b82f6; padding-bottom: 0.3em; } .article-content h2 { font-size: 1.8em; font-weight: bold; margin: 1.3em 0 0.4em 0; color: #3b82f6; } .article-content h3 { font-size: 1.4em; margin: 1em 0 0.3em 0; } .article-content p { margin: 1em 0; } .article-content code { background-color: #f5f5f5; border-radius: 3px; padding: 2px 6px; font-family: 'Monaco', 'Courier New', monospace; } .article-content pre { background-color: #1e1e1e; color: #d4d4d4; padding: 1em; border-radius: 5px; overflow-x: auto; margin: 1em 0; } .article-content blockquote { border-left: 4px solid #3b82f6; padding-left: 1em; margin-left: 0; color: #666; }`,
      preview: '📄 简洁蓝色风格'
    },
    {
      name: '专业深色',
      id: 'dark-pro',
      cssContent: `.article-content { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.9; color: #e0e0e0; background: #1a1a1a; padding: 2em; border-radius: 8px; } .article-content h1 { font-size: 2.4em; font-weight: 700; margin: 1.5em 0 0.5em 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; } .article-content h2 { font-size: 2em; margin: 1.3em 0 0.4em 0; color: #667eea; } .article-content p { margin: 1.2em 0; } .article-content code { background: #2a2a2a; color: #00d084; padding: 3px 8px; border-radius: 4px; } .article-content pre { background: #0d0d0d; border-left: 3px solid #667eea; padding: 1.2em; border-radius: 6px; }`,
      preview: '🌙 专业深色'
    },
    {
      name: '学术风格',
      id: 'academic',
      cssContent: `.article-content { font-family: 'Georgia', 'Garamond', serif; line-height: 2; color: #2c3e50; max-width: 900px; margin: 0 auto; } .article-content h1 { font-size: 2.5em; font-weight: 700; color: #1a1a1a; margin: 1em 0 0.5em 0; border-bottom: 1px solid #bdc3c7; padding-bottom: 0.5em; } .article-content h2 { font-size: 2em; color: #34495e; margin-top: 1.5em; } .article-content p { text-align: justify; } .article-content code { background: #ecf0f1; padding: 2px 5px; font-family: 'Courier New', monospace; } .article-content blockquote { border-left: 5px solid #34495e; padding-left: 1.5em; color: #555; font-style: italic; }`,
      preview: '📚 学术风格'
    },
    {
      name: '炫彩渐变',
      id: 'gradient',
      cssContent: `.article-content { font-family: 'Poppins', 'Arial', sans-serif; line-height: 1.8; color: #333; } .article-content h1 { font-size: 2.5em; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; font-weight: 800; margin: 1em 0; } .article-content h2 { background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; } .article-content p { margin: 1.2em 0; } .article-content a { color: #f5576c; text-decoration: underline; } .article-content code { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 2px 8px; border-radius: 3px; }`,
      preview: '🎨 炫彩渐变'
    }
  ]);

  const currentTheme = ref<Theme>(themes.value[0]);

  const selectTheme = (themeId: string) => {
    const theme = themes.value.find(t => t.id === themeId);
    if (theme) {
      currentTheme.value = theme;
    }
  };

  return {
    themes,
    currentTheme,
    selectTheme
  };
});
