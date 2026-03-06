import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export interface ThemeConfig {
  name: string
  colors: {
    primary: string
    secondary: string
    accent: string
    background: string
    text: string
  }
}

export const useThemeStore = defineStore('theme', () => {
  const isDarkMode = ref(localStorage.getItem('darkMode') === 'true')
  const currentTheme = ref(localStorage.getItem('theme') || 'default')

  const themes: Record<string, any> = {
    default: {
      name: '默认蓝',
      colors: {
        primary: '#409eff',
        secondary: '#67c23a',
        accent: '#e6a23c'
      }
    },
    dark: {
      name: '极客黑',
      colors: {
        primary: '#409eff',
        secondary: '#67c23a',
        accent: '#e6a23c'
      }
    },
    academic: {
      name: '学术蓝',
      colors: {
        primary: '#1e3a8a',
        secondary: '#1e40af',
        accent: '#3b82f6'
      }
    },
    vibrant: {
      name: '珊瑚红',
      colors: {
        primary: '#ff6b6b',
        secondary: '#4ecdc4',
        accent: '#95e1d3'
      }
    },
    forest: {
      name: '森林绿',
      colors: {
        primary: '#2d6a4f',
        secondary: '#40916c',
        accent: '#95d5b2'
      }
    }
  }

  const applyTheme = () => {
    const html = document.documentElement
    
    // 1. 处理深色模式类
    if (isDarkMode.value) {
      html.classList.add('dark')
    } else {
      html.classList.remove('dark')
    }

    // 2. 清理旧的行内颜色变量，防止污染
    const oldKeys = ['primary', 'secondary', 'accent', 'background', 'text']
    oldKeys.forEach(key => html.style.removeProperty(`--color-${key}`))

    // 3. 注入当前主题的品牌核心色
    const themeConfig = themes[currentTheme.value] || themes.default
    if (themeConfig && themeConfig.colors) {
      Object.entries(themeConfig.colors).forEach(([key, value]) => {
        const colorValue = value as string
        html.style.setProperty(`--color-${key}`, colorValue)
        
        // 同时同步 Element Plus 品牌主色
        if (key === 'primary') {
          html.style.setProperty('--el-color-primary', colorValue)
        }
      })
    }
  }

  // 监听状态变化
  watch([isDarkMode, currentTheme], () => {
    localStorage.setItem('darkMode', String(isDarkMode.value))
    localStorage.setItem('theme', currentTheme.value)
    applyTheme()
  }, { immediate: true })

  const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
  }

  const setTheme = (themeName: string) => {
    if (themes[themeName]) {
      currentTheme.value = themeName
      // 如果选择了深色主题，自动开启深色模式开关（体验更好）
      if (themeName === 'dark') {
        isDarkMode.value = true
      }
    }
  }

  return {
    isDarkMode,
    currentTheme,
    themes,
    toggleDarkMode,
    setTheme,
    applyTheme
  }
})
