<template>
  <div class="changelog-page">
    <div class="page-header">
      <h1>更新日志</h1>
      <p class="subtitle">记录每一次进化，见证我们的成长历程</p>
    </div>

    <div class="timeline-container">
      <el-timeline>
        <!-- v1.2.0: 自动化与凭证修复 -->
        <el-timeline-item timestamp="2026-03-07" placement="top" type="primary" size="large">
          <el-card class="changelog-card">
            <template #header>
              <div class="version-header">
                <h3>v1.2.0 - 自动化引擎与发布深度优化</h3>
                <el-tag type="success">最新</el-tag>
              </div>
            </template>
            <div class="changelog-content">
              <h4>🚀 新功能</h4>
              <ul>
                <li><strong>浏览器辅助登录助手</strong>：引入 Playwright 自动化技术，支持一键引导用户登录并截获凭证，彻底解决 Token 获取难题。</li>
                <li><strong>全新平台配置中心</strong>：重构了平台管理 UI，集成知乎蓝、掘金蓝等高清品牌图标，交互更直观。</li>
              </ul>
              <h4>🛠️ 关键修复</h4>
              <ul>
                <li><strong>发布凭证同步修复</strong>：重构后端凭证分发逻辑，解决 `credentials` 字段断供问题，知乎发布流程现已 100% 走通。</li>
                <li><strong>接口闭环</strong>：补全了 `ArticleController` 中遗留的发布 TODO 接口，确保全量 API 均可正常调用。</li>
                <li><strong>并发安全增强</strong>：发布结果容器升级为 `ConcurrentHashMap`，极大提升了多平台群发时的稳定性。</li>
              </ul>
            </div>
          </el-card>
        </el-timeline-item>

        <!-- v1.1.0: 深色模式优化 -->
        <el-timeline-item timestamp="2026-03-06" placement="top">
          <el-card class="changelog-card">
            <template #header>
              <div class="version-header">
                <h3>v1.1.0 - 极致深色模式与 UI 融合</h3>
              </div>
            </template>
            <div class="changelog-content">
              <h4>🎨 视觉升级</h4>
              <ul>
                <li><strong>沉浸式深色模式</strong>：深度优化全局配色方案，消除 stark white 突兀感，编辑器与全站 UI 完美融合。</li>
                <li><strong>玻璃拟态效果</strong>：登录页与注册页引入高级 Glassmorphism 设计，打造未来感视觉。</li>
              </ul>
              <h4>⚙️ 系统增强</h4>
              <ul>
                <li><strong>主题系统解耦</strong>：重构 `themeStore`，实现品牌颜色与深浅模式独立切换，互不冲突。</li>
                <li><strong>Markdown 编辑器优化</strong>：实现了编辑器暗黑主题的动态跟随，阅读体验更出色。</li>
              </ul>
            </div>
          </el-card>
        </el-timeline-item>

        <!-- v1.0.0: 初始版本 -->
        <el-timeline-item timestamp="2026-03-01" placement="top">
          <el-card class="changelog-card">
            <template #header>
              <div class="version-header">
                <h3>v1.0.0 - 核心发布引擎诞生</h3>
              </div>
            </template>
            <div class="changelog-content">
              <h4>🏗️ 核心成就</h4>
              <ul>
                <li><strong>全栈架构搭建</strong>：完成 Spring Boot 3 + Vue 3 + MySQL 的核心框架集成。</li>
                <li><strong>多平台分发引擎</strong>：初步实现微信、掘金、知乎等主流平台的适配器架构。</li>
                <li><strong>文章管理系统</strong>：支持 Markdown 编辑、多图上传与本地草稿存储。</li>
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
