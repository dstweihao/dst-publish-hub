<template>
  <div class="changelog-page">
    <div class="page-header">
      <h1>{{ $t('changelog.title') }}</h1>
      <p class="subtitle">{{ $t('changelog.subtitle') }}</p>
    </div>

    <div class="timeline-container">
      <el-timeline>
        <!-- v1.2.0 -->
        <el-timeline-item timestamp="2026-03-07" placement="top" type="primary" size="large">
          <el-card class="changelog-card">
            <template #header>
              <div class="version-header">
                <h3>{{ $t('changelog.v1_2_0.title') }}</h3>
                <el-tag type="success">{{ $t('changelog.latest') }}</el-tag>
              </div>
            </template>
            <div class="changelog-content">
              <h4>{{ $t('changelog.v1_2_0.newFeatures') }}</h4>
              <ul>
                <li><strong>{{ $t('changelog.v1_2_0.feat1Title') }}</strong>：{{ $t('changelog.v1_2_0.feat1Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_2_0.feat2Title') }}</strong>：{{ $t('changelog.v1_2_0.feat2Desc') }}</li>
              </ul>
              <h4>{{ $t('changelog.v1_2_0.fixes') }}</h4>
              <ul>
                <li><strong>{{ $t('changelog.v1_2_0.fix1Title') }}</strong>：{{ $t('changelog.v1_2_0.fix1Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_2_0.fix2Title') }}</strong>：{{ $t('changelog.v1_2_0.fix2Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_2_0.fix3Title') }}</strong>：{{ $t('changelog.v1_2_0.fix3Desc') }}</li>
              </ul>
            </div>
          </el-card>
        </el-timeline-item>

        <!-- v1.1.0 -->
        <el-timeline-item timestamp="2026-03-06" placement="top">
          <el-card class="changelog-card">
            <template #header>
              <div class="version-header">
                <h3>{{ $t('changelog.v1_1_0.title') }}</h3>
              </div>
            </template>
            <div class="changelog-content">
              <h4>{{ $t('changelog.v1_1_0.visual') }}</h4>
              <ul>
                <li><strong>{{ $t('changelog.v1_1_0.vis1Title') }}</strong>：{{ $t('changelog.v1_1_0.vis1Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_1_0.vis2Title') }}</strong>：{{ $t('changelog.v1_1_0.vis2Desc') }}</li>
              </ul>
              <h4>{{ $t('changelog.v1_1_0.system') }}</h4>
              <ul>
                <li><strong>{{ $t('changelog.v1_1_0.sys1Title') }}</strong>：{{ $t('changelog.v1_1_0.sys1Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_1_0.sys2Title') }}</strong>：{{ $t('changelog.v1_1_0.sys2Desc') }}</li>
              </ul>
            </div>
          </el-card>
        </el-timeline-item>

        <!-- v1.0.0 -->
        <el-timeline-item timestamp="2026-03-01" placement="top">
          <el-card class="changelog-card">
            <template #header>
              <div class="version-header">
                <h3>{{ $t('changelog.v1_0_0.title') }}</h3>
              </div>
            </template>
            <div class="changelog-content">
              <h4>{{ $t('changelog.v1_0_0.core') }}</h4>
              <ul>
                <li><strong>{{ $t('changelog.v1_0_0.ach1Title') }}</strong>：{{ $t('changelog.v1_0_0.ach1Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_0_0.ach2Title') }}</strong>：{{ $t('changelog.v1_0_0.ach2Desc') }}</li>
                <li><strong>{{ $t('changelog.v1_0_0.ach3Title') }}</strong>：{{ $t('changelog.v1_0_0.ach3Desc') }}</li>
              </ul>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup lang="ts">
// 更新日志页面逻辑简单，目前无需复杂状态
</script>

<style scoped>
.changelog-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 3rem;
  text-align: center;
}

.changelog-page h1 {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-warning));
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: var(--el-text-color-secondary);
  font-size: 18px;
}

.timeline-container {
  padding: 0 1rem;
}

.changelog-card {
  border-radius: 16px;
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.3s ease;
  overflow: hidden;
}

.changelog-card:hover {
  transform: translateX(10px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.changelog-content h4 {
  margin: 1.5rem 0 0.75rem 0;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.changelog-content ul {
  padding-left: 1.25rem;
  margin: 0;
}

.changelog-content li {
  margin-bottom: 10px;
  line-height: 1.6;
  color: var(--el-text-color-regular);
}

.changelog-content strong {
  color: var(--el-color-primary);
}

/* 适配深色模式 */
:deep(.el-card) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.el-timeline-item__node--primary) {
  box-shadow: 0 0 10px var(--el-color-primary-light-3);
}
</style>
