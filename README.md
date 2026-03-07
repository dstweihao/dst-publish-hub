<div align="center">
  <img src="./logo.png" alt="DST Publish Hub Logo" width="120" style="border-radius: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); margin-bottom: 20px;" />
  <h1>DST Publish Hub (一稿通)</h1>

  <p>
    <strong>A high-performance, multi-platform Markdown publishing engine supporting 16+ portals.</strong><br>
    <strong>高性能多平台 Markdown 聚合分发引擎，支持 16+ 个核心内容平台同步发布。</strong>
  </p>

  <p>
    <a href="https://github.com/vuejs/vue"><img src="https://img.shields.io/badge/Vue-3.x-42b883?style=flat-square&logo=vue.js" alt="Vue"></a>
    <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=flat-square&logo=spring-boot" alt="Spring Boot"></a>
    <a href="https://www.typescriptlang.org/"><img src="https://img.shields.io/badge/TypeScript-Active-3178C6?style=flat-square&logo=typescript" alt="TypeScript"></a>
    <a href="https://github.com/"><img src="https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square" alt="License"></a>
    <a href="https://github.com"><img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square" alt="PRs Welcome"></a>
  </p>
  
  [English](#english) | [简体中文](#简体中文)
</div>

---

<br>

## 🌐 English

DST Publish Hub is an automated article publishing system tailored for content creators, independent developers, and tech writers. Write your content once using our rich-theme Markdown editor, and reliably distribute it to over 16 platforms with a single click.

### ✨ Features
- **Write Once, Publish Everywhere**: Fully automated headless browser distribution.
- **16+ Platform Support**: Substack, Medium, Wechat, Juejin, Zhihu, and more.
- **Rich Markdown Editing**: Real-time rendering, multi-theme layouts (e.g., GitHub, VuePress, MK-Cute), and intelligent dark mode.
- **Anti-Bot Circumvention**: Bypasses strict detection mechanisms via Playwright (automation concealment, clipboard injection, random human delays).
- **Task Scheduling**: Plan and automate your publications on your own timeline.
- **Analytics Dashboard**: Aggregate metrics seamlessly.

### 🛠 Tech Stack
- **Frontend**: Vue 3, TypeScript, Element Plus, Pinia, md-editor-v3.
- **Backend**: Java 19, Spring Boot 3.2, Spring Security (JWT), Spring Data JPA, MySQL 8.
- **Automation**: Playwright (Java port) for ultra-stable headless/headed browser interactions.

### 🚀 Getting Started
```bash
# Frontend
cd frontend
npm install
npm run dev

# Backend
cd backend
mvn clean spring-boot:run
```

---

<br>

## 🇨🇳 简体中文

**DST Publish Hub (一稿通)** 是一款专为技术博主、独立开发者和自媒体创作者打造的自动化图文分发引擎。在内置的 Markdown 编辑器中完成创作后，可一键将携带精美排版（支持代码高亮与主题切换）的文章同步发布至全网 16 大平台。

### ✨ 核心特性
- **一键分发体系**：告别繁琐的复制粘贴，通过自动化浏览器直接将文章推送到平台后管发稿。
- **16 大主流平台全面支持**：
  - **技术社区**：稀土掘金、博客园、CSDN、开源中国、思否 (SegmentFault)、Hashnode、Dev.to
  - **大众资讯**：知乎、微信公众号、今日头条、百家号、微博、 Medium、小红书、豆瓣、简书
- **极致的 Markdown 体验**：
  - 接入领先的 `md-editor-v3`，实时预览。
  - 内置多套排版主题（如 GitHub 黑白风、VuePress 清新风、科技蓝、唯美青等）。
  - 支持将服务端精准渲染的 HTML 甚至图片直推剪贴板，彻底打破各平台富文本编辑器的底层隔离。
- **自研反风控（Anti-Bot）策略**：
  - 基于 Playwright 的深度指纹抹除（去除 `navigator.webdriver` 等痕迹）。
  - 全链路模拟真人视口、输入延迟与操作停顿，突破最严苛的图形验证码限制。
- **数据与安全**：全站受 Spring Security 和 JWT 令牌保护，文章排期与定时发布无忧。

### 🛠 技术架构
- **前端驱动**：Vue 3 + TypeScript + Pinia 状态管理 + Element Plus 组件库。
- **后端中枢**：Spring Boot 3 + Java 19 高级特性底座 + CommonMark 服务端渲染。
- **自动化核心**：微软开源生态力作 `Playwright` 构建的全拟真发文矩阵。
- **持久层**：MySQL 8 存储介质结合 Hibernate JPA 自动化管理。

### 🚀 快速上手

#### 1. 启动前端
```bash
cd frontend
npm install
npm run dev
```
> 前端服务将在 `http://localhost:5173` 启动。

#### 2. 启动后端
```bash
cd backend
# 建议配置环境变量 PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 跳过繁重的浏览器下载（如已具备运行环境）
mvn clean spring-boot:run
```
> 后端 API 服务将在 `http://localhost:8080` 启动。

### 🤝 贡献规范
我们非常期待并欢迎社区的参与！如果您对开源和技术有着同样的执着：
- 提交 `Pull Request` 前，请确保已执行完整的本地 E2E 测试和打包校验。
- 建议为新增平台提供对应的自动化单元拦截与鉴权凭证解析逻辑。

### 📄 开源许可证
本项目遵循 [MIT License](LICENSE) 开源代码协议。您可以随意修改和商用，但请保留必要的作者标识。

---
*Built with ❤️ by HaoWei Team. Empower your content distribution worldwide.*
