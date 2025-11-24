<template>
  <div id="app" :class="{ dark: isDarkMode }">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore } from '@/stores/themeStore'

const themeStore = useThemeStore()
const isDarkMode = computed(() => themeStore.isDarkMode)
</script>

<style scoped>
#app {
  transition: background-color 0.3s, color 0.3s;
}
</style>
