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

  const themes: Record<string, ThemeConfig> = {
    default: {
      name: '默认蓝',
      colors: {
        primary: '#409eff',
        secondary: '#67c23a',
        accent: '#e6a23c',
        background: '#ffffff',
        text: '#303133'
      }
    },
    dark: {
      name: '极客黑',
      colors: {
        primary: '#409eff',
        secondary: '#67c23a',
        accent: '#e6a23c',
        background: '#141414',
        text: '#e4e7eb'
      }
    },
    academic: {
      name: '学术蓝',
      colors: {
        primary: '#1e3a8a',
        secondary: '#1e40af',
        accent: '#3b82f6',
        background: '#f8fafc',
        text: '#0f172a'
      }
    },
    vibrant: {
      name: '珊瑚红',
      colors: {
        primary: '#ff6b6b',
        secondary: '#4ecdc4',
        accent: '#95e1d3',
        background: '#fff5f7',
        text: '#2d3436'
      }
    },
    forest: {
      name: '森林绿',
      colors: {
        primary: '#2d6a4f',
        secondary: '#40916c',
        accent: '#95d5b2',
        background: '#f7fdfb',
        text: '#1b4332'
      }
    }
  }

  const applyTheme = () => {
    // 处理深色模式类
    const html = document.documentElement
    if (isDarkMode.value) {
      html.classList.add('dark')
    } else {
      html.classList.remove('dark')
    }

    // 注入颜色变量
    const config = themes[currentTheme.value] || themes.default
    Object.entries(config.colors).forEach(([key, value]) => {
      html.style.setProperty(`--color-${key}`, value)
      // 同时兼容 Element Plus 的变量名
      if (key === 'primary') {
        html.style.setProperty('--el-color-primary', value)
      }
    })
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
