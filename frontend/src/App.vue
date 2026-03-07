<template>
  <el-config-provider :locale="currentElementLocale">
    <div id="app">
      <router-view />
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useThemeStore } from '@/stores/themeStore'
import { useI18n } from 'vue-i18n'

const themeStore = useThemeStore()
const { getLocaleMessage, locale } = useI18n()

// 动态获取当前选定语言对应的 Element Plus 语言包
const currentElementLocale = computed(() => {
  return (getLocaleMessage(locale.value) as any).el
})

onMounted(() => {
  themeStore.applyTheme()
})
</script>

<style scoped>
#app {
  transition: background-color 0.3s, color 0.3s;
  min-height: 100vh;
}
</style>
