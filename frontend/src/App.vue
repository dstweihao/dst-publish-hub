<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useThemeStore } from '@/stores/themeStore'

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.applyTheme()
})
</script>

<style scoped>
#app {
  transition: background-color 0.3s, color 0.3s;
}
</style>
