# ArticleHub Backend - 多平台文章发布系统

## 项目说明

这是一个基于Spring Boot 3.2 + Spring Data JPA + JWT认证的多平台文章发布系统后端。支持一键发布文章到微信公众号、稀土掘金、博客园、知乎、CSDN、今日头条、简书等7个平台。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- OkHttp 4.x
- MapStruct
- Lombok
- Maven

## 项目结构

\`\`\`
backend/
├── src/main/java/com/articlehub/
│   ├── entity/              # JPA实体类
│   ├── dto/                 # 数据传输对象
│   ├── repository/          # 数据访问层
│   ├── service/             # 业务逻辑层
│   ├── controller/          # API控制层
│   ├── platform/            # 平台集成模块
│   ├── security/            # 安全认证
│   ├── util/                # 工具类
│   ├── config/              # 配置类
│   └── ArticlePublishApplication.java
├── src/main/resources/
│   ├── application.yml      # 应用配置
│   └── logback-spring.xml   # 日志配置
├── sql/
│   └── init.sql             # 数据库初始化脚本
├── pom.xml
└── README.md
\`\`\`

## 快速开始

### 1. 数据库初始化

\`\`\`bash
mysql -u root -p < sql/init.sql
\`\`\`

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息和JWT密钥：

\`\`\`yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/article_hub
    username: root
    password: your_password

jwt:
  secret: your-secure-secret-key-at-least-64-characters
  expiration: 86400000  # 24小时
\`\`\`

### 3. 编译运行

\`\`\`bash
mvn clean install
mvn spring-boot:run
\`\`\`

应用将在 `http://localhost:8080/api` 启动。

## API文档

### 认证接口

#### 注册
\`\`\`
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}
\`\`\`

#### 登录
\`\`\`
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "code": 0,
  "message": "Success",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "avatar": null,
      "createdAt": "2025-01-18T10:00:00"
    }
  }
}
\`\`\`

### 文章接口

#### 获取我的文章
\`\`\`
GET /api/articles?page=0&size=10
Authorization: Bearer {token}
\`\`\`

#### 创建文章
\`\`\`
POST /api/articles
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "文章标题",
  "content": "# 文章内容\n...",
  "description": "文章描述",
  "theme": "default",
  "tags": ["java", "spring"]
}
\`\`\`

#### 发布文章
\`\`\`
POST /api/publish/article/{articleId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "platforms": ["wechat", "juejin", "csdn"]
}

Response:
{
  "code": 0,
  "message": "Success",
  "data": {
    "wechat": {
      "success": true,
      "publishedUrl": "https://..."
    },
    "juejin": {
      "success": true,
      "publishedUrl": "https://juejin.cn/post/..."
    },
    ...
  }
}
\`\`\`

## 平台配置

### 微信公众号
需要配置：AppID、AppSecret

### 稀土掘金
需要配置：API Token

### 博客园
需要配置：账号、API Token

### CSDN
需要配置：账号、API Key

### 知乎
需要配置：账号、认证Token

### 今日头条
需要配置：账号、API Key

### 简书
需要配置：账号、API Token

## 开发建议

1. **安全性**：在生产环境中更改JWT密钥和数据库密码
2. **错误处理**：完善异常处理和日志记录
3. **性能优化**：添加缓存层（Redis）提高性能
4. **测试**：编写单元测试和集成测试
5. **监控**：集成监控系统（如Prometheus）

## 许可证

MIT
