import { defineStore } from 'pinia'
import { ref } from 'vue'

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
      name: '默认主题',
      colors: {
        primary: '#409eff',
        secondary: '#67c23a',
        accent: '#e6a23c',
        background: '#ffffff',
        text: '#303133'
      }
    },
    dark: {
      name: '深色专业',
      colors: {
        primary: '#409eff',
        secondary: '#67c23a',
        accent: '#e6a23c',
        background: '#141414',
        text: '#e4e7eb'
      }
    },
    academic: {
      name: '学术主题',
      colors: {
        primary: '#1e3a8a',
        secondary: '#1e40af',
        accent: '#3b82f6',
        background: '#f8fafc',
        text: '#0f172a'
      }
    },
    vibrant: {
      name: '炫彩渐变',
      colors: {
        primary: '#ff6b6b',
        secondary: '#4ecdc4',
        accent: '#95e1d3',
        background: '#fff5f7',
        text: '#2d3436'
      }
    }
  }

  const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
    localStorage.setItem('darkMode', String(isDarkMode.value))
  }

  const setTheme = (themeName: string) => {
    if (themes[themeName]) {
      currentTheme.value = themeName
      localStorage.setItem('theme', themeName)
    }
  }

  return {
    isDarkMode,
    currentTheme,
    themes,
    toggleDarkMode,
    setTheme
  }
})
