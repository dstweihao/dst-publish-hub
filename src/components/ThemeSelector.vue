<template>
  <div class="theme-selector">
    <h3 class="text-lg font-semibold text-white mb-4">选择主题</h3>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div 
        v-for="theme in themes"
        :key="theme.id"
        @click="selectTheme(theme.id)"
        :class="[
          'p-4 rounded-lg cursor-pointer transition-all',
          currentTheme.id === theme.id 
            ? 'bg-blue-600 border-2 border-blue-400' 
            : 'bg-gray-800 border-2 border-gray-700 hover:border-blue-500'
        ]"
      >
        <div class="text-white font-semibold mb-2">{{ theme.name }}</div>
        <div class="text-gray-300 text-sm">{{ theme.preview }}</div>
        <div class="mt-3 p-3 bg-gray-900 rounded text-xs text-gray-300">
          <div>预览文字示例</div>
          <div class="font-bold mt-1">标题预览</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/stores/themeStore';

const themeStore = useThemeStore();

const selectTheme = (themeId: string) => {
  themeStore.selectTheme(themeId);
};

const themes = themeStore.themes;
const currentTheme = themeStore.currentTheme;
</script>
