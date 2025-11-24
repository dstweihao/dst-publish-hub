# 多平台发布系统 - 平台集成指南

## 📊 平台总览

本系统支持 **16 个内容发布平台**，覆盖技术、社交、生活方式等多个领域。

### 平台分类

#### 🔬 技术类平台 (7个)

| 平台 | 标识符 | 特点 | 认证方式 |
|------|--------|------|--------|
| **CSDN** | `csdn` | 全球最大中文IT社区，日流量百万 | API Token |
| **稀土掘金** | `juejin` | 高质量开发者社区，算法推荐 | API Key |
| **博客园** | `cnblogs` | 专业IT技术社区，历史悠久 | API Token |
| **开源中国** | `oschina` | 开源社区，资源丰富 | OAuth2 |
| **思否** | `segmentfault` | 技术问答社区，互动性强 | API Key |
| **Dev.to** | `devto` | 全球开发者社区，英文 | API Key |
| **Hashnode** | `hashnode` | 开发者博客平台，国际化 | GraphQL API |

#### 📱 综合内容平台 (4个)

| 平台 | 标识符 | 特点 | 内容形式 |
|------|--------|------|--------|
| **微信公众号** | `wechat` | 官方内容平台，传播力强 | 图文消息 |
| **今日头条** | `toutiao` | 内容分发平台，推荐精准 | 长文章 |
| **百家号** | `baidu` | 百度内容平台，搜索加权 | 文章 |
| **微博** | `weibo` | 社交媒体，实时性强 | 微博文案 |

#### ❤️ 生活方式平台 (4个)

| 平台 | 标识符 | 特点 | 受众 |
|------|--------|------|------|
| **知乎** | `zhihu` | 中文知识内容平台 | 知识工作者 |
| **简书** | `jianshu` | 创意写作社区 | 创意工作者 |
| **小红书** | `xiaohongshu` | 生活方式分享社区 | 年轻用户 |
| **豆瓣** | `douban` | 电影评论与社区 | 影迷、书迷 |

#### 🌍 国际平台 (1个)

| 平台 | 标识符 | 特点 | 语言 |
|------|--------|------|------|
| **Medium** | `medium` | 国际内容发布平台 | 英文 |

---

## 🔐 认证配置

### 1. **CSDN** - API Token
\`\`\`
认证方式: Bearer Token
获取方式: CSDN开放平台 → 个人中心 → API Token
\`\`\`

### 2. **稀土掘金** - API Key
\`\`\`
认证方式: Bearer Token
获取方式: 掘金控制台 → API管理 → 生成新Token
\`\`\`

### 3. **博客园** - API Token
\`\`\`
认证方式: Bearer Token
获取方式: 博客园 → 设置 → API密钥
\`\`\`

### 4. **开源中国** - OAuth2
\`\`\`
认证方式: OAuth2 Access Token
获取方式: 开源中国 → 账户 → 应用管理 → 创建应用
\`\`\`

### 5. **思否** - API Key
\`\`\`
认证方式: Bearer Token
获取方式: 思否 → 个人中心 → API设置
\`\`\`

### 6. **Dev.to** - API Key
\`\`\`
认证方式: API Key (Header: api-key)
获取方式: dev.to → Settings → Extensions → API Keys
\`\`\`

### 7. **Hashnode** - GraphQL Token
\`\`\`
认证方式: GraphQL API Token
获取方式: Hashnode → Settings → Developer → API Keys
\`\`\`

### 8. **微信公众号** - AppID & AppSecret
\`\`\`
认证方式: OAuth2 + AppID/AppSecret
获取方式: 公众号后台 → 开发 → 基本配置
\`\`\`

### 9. **今日头条** - API Token
\`\`\`
认证方式: Bearer Token
获取方式: 头条号后台 → 设置 → API管理
\`\`\`

### 10. **百家号** - Access Token
\`\`\`
认证方式: Access Token
获取方式: 百家号后台 → 开发者 → 接入管理
\`\`\`

### 11. **微博** - Access Token
\`\`\`
认证方式: OAuth2 Access Token
获取方式: 微博开放平台 → 应用管理 → 获取Token
\`\`\`

### 12. **知乎** - OAuth2 Token
\`\`\`
认证方式: OAuth2
获取方式: 知乎开放平台 → 应用管理
\`\`\`

### 13. **简书** - API Token
\`\`\`
认证方式: Bearer Token
获取方式: 简书 → 账户 → API令牌
\`\`\`

### 14. **小红书** - OAuth2
\`\`\`
认证方式: OAuth2 + API Key
获取方式: 小红书创作平台 → 开发者工具
\`\`\`

### 15. **豆瓣** - API Key
\`\`\`
认证方式: API Key
获取方式: 豆瓣API → 应用管理 → 新增应用
\`\`\`

### 16. **Medium** - API Token
\`\`\`
认证方式: Bearer Token
获取方式: Medium → Settings → Integration tokens
\`\`\`

---

## 🚀 发布逻辑架构

### 核心设计模式

\`\`\`
┌─────────────────────────────────┐
│   发布请求 (PublishController)   │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│  发布服务 (PublishService)       │
│  - 验证文章                      │
│  - 转换格式                      │
│  - 并发发布                      │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│  平台工厂 (PlatformFactory)      │
│  - 获取对应平台发布器             │
│  - 管理发布器生命周期             │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│  平台发布器 (PlatformPublisher)  │
│  - 具体实现发布逻辑              │
│  - API调用和异常处理             │
└─────────────────────────────────┘
\`\`\`

### 异步发布流程

1. **接收发布请求** → ArticleController
2. **验证文章数据** → ArticleService
3. **并发发布** → PublishService (使用 CompletableFuture)
4. **调用平台API** → 各平台 Publisher
5. **保存发布记录** → PlatformPublish 表
6. **返回发布结果** → 前端展示统计

### 失败重试机制

\`\`\`java
// 自动重试配置
@Retry(maxAttempts = 3, delay = 1000)
public PublishResult publish(...) {
    // 发布逻辑
}
\`\`\`

---

## 📋 发布结果统计

发布完成后，系统返回：

\`\`\`json
{
  "totalCount": 16,
  "successCount": 14,
  "failureCount": 2,
  "publishedUrls": {
    "csdn": "https://blog.csdn.net/user/article/123",
    "juejin": "https://juejin.cn/post/456",
    ...
  },
  "failedPlatforms": [
    {
      "platform": "medium",
      "reason": "Authentication failed"
    },
    {
      "platform": "hashnode",
      "reason": "Network timeout"
    }
  ]
}
\`\`\`

---

## 🛠️ 扩展新平台

### 第1步：创建新的Publisher类

\`\`\`java
@Service
public class NewPlatformPublisher implements PlatformPublisher {
    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        // 实现发布逻辑
    }

    @Override
    public String getPlatformName() {
        return "newplatform";  // 返回平台标识符
    }
}
\`\`\`

### 第2步：更新前端平台列表

\`\`\`typescript
// src/types/platform.ts
export type PlatformType = ... | 'newplatform';

export const PLATFORMS: Platform[] = [
  ...,
  {
    id: 'newplatform',
    name: '新平台',
    icon: '🆕',
    category: 'tech',
    ...
  }
];
\`\`\`

### 第3步：添加认证配置（如需要）

在数据库 `platform_account` 表添加新平台的账户配置。

---

## 🎯 最佳实践

### 1. 内容适配

不同平台对内容有不同要求：
- **技术平台**: 支持Markdown，重视代码块
- **社交平台**: 字数限制，需要配图
- **生活方式**: 重视排版，支持多媒体

### 2. 发布时间

建议使用分布式定时任务：
\`\`\`java
@Scheduled(cron = "0 0 9 * * ?")  // 每天9点发布
public void scheduledPublish() {
    // 发布逻辑
}
\`\`\`

### 3. 错误处理

\`\`\`java
if (!result.isSuccess()) {
    log.error("Platform {} publish failed: {}", 
        platform, result.getErrorMessage());
    // 发送告警通知
    notificationService.alertPublishFailure(platform);
}
\`\`\`

### 4. 性能优化

- 使用线程池并发发布
- 实现发布队列缓冲
- 缓存认证Token

---

## 📞 常见问题

**Q: 如何处理平台API变更？**  
A: 检查平台官方文档，更新对应Publisher中的API端点和字段映射。

**Q: 多个平台同时发布会很慢吗？**  
A: 不会。系统使用CompletableFuture并发发布，通常不超过3秒。

**Q: 支持定时发布吗？**  
A: 支持。通过Quartz或Spring Scheduler实现定时任务。

**Q: 如何处理文章格式不兼容？**  
A: 每个Publisher在内部转换为目标平台支持的格式。

---

## 📈 后续计划

- [ ] 支持小红书API（当API正式开放）
- [ ] 支持抖音开放平台
- [ ] 支持YouTube
- [ ] 视频内容发布支持
- [ ] 草稿自动保存
- [ ] 发布效果分析
