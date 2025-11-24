<template>
  <div class="space-y-6">
    <!-- 平台分类标签 -->
    <div class="flex gap-2 flex-wrap">
      <button
        v-for="category in categories"
        :key="category"
        @click="selectedCategory = category"
        :class="[
          'px-4 py-2 rounded-lg font-medium transition-colors',
          selectedCategory === category
            ? 'bg-primary text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-300'
        ]"
      >
        {{ getCategoryLabel(category) }}
      </button>
    </div>

    <!-- 平台网格 -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div
        v-for="platform in filteredPlatforms"
        :key="platform.id"
        @click="selectPlatform(platform)"
        :class="[
          'p-4 rounded-lg border-2 cursor-pointer transition-all',
          selectedPlatforms.includes(platform.id)
            ? 'border-primary bg-primary/10'
            : 'border-gray-200 hover:border-primary dark:border-gray-700'
        ]"
      >
        <div class="text-3xl mb-2">{{ platform.icon }}</div>
        <h3 class="font-bold text-sm mb-1">{{ platform.name }}</h3>
        <p class="text-xs text-gray-600 dark:text-gray-400">{{ platform.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { PLATFORMS, type PlatformType } from '@/types/platform';

const selectedCategory = ref<'all' | 'tech' | 'social' | 'lifestyle' | 'international'>('all');
const selectedPlatforms = ref<PlatformType[]>([]);

const categories = [
  { value: 'all', label: '全部' },
  { value: 'tech', label: '技术平台' },
  { value: 'social', label: '综合内容' },
  { value: 'lifestyle', label: '生活方式' },
  { value: 'international', label: '国际平台' },
];

const filteredPlatforms = computed(() => {
  if (selectedCategory.value === 'all') {
    return PLATFORMS;
  }
  return PLATFORMS.filter(p => p.category === selectedCategory.value);
});

const getCategoryLabel = (category: string) => {
  return categories.find(c => c.value === category)?.label || category;
};

const selectPlatform = (platform: any) => {
  const index = selectedPlatforms.value.indexOf(platform.id);
  if (index > -1) {
    selectedPlatforms.value.splice(index, 1);
  } else {
    selectedPlatforms.value.push(platform.id);
  }
};

defineExpose({
  selectedPlatforms,
});
</script>
