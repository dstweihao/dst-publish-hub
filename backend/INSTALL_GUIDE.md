# 文章发布平台 - 后端安装和运行指南

## 📋 系统要求

- **Java**: JDK 17 或更高版本
- **数据库**: MySQL 8.0 或更高版本
- **构建工具**: Maven 3.8.0 或更高版本
- **操作系统**: Windows / macOS / Linux

## 🚀 快速开始

### 1. 克隆或下载项目

\`\`\`bash
# 如果使用 Git
git clone <repository-url>
cd article-publish-platform/backend

# 或直接使用提供的代码
cd backend
\`\`\`

### 2. 配置数据库

#### 使用 MySQL

**步骤 1**: 安装 MySQL（如未安装）

\`\`\`bash
# macOS (使用 Homebrew)
brew install mysql

# Windows - 下载安装程序
# https://dev.mysql.com/downloads/mysql/

# Linux (Ubuntu)
sudo apt-get install mysql-server
\`\`\`

**步骤 2**: 启动 MySQL 服务

\`\`\`bash
# macOS
brew services start mysql

# Linux
sudo service mysql start

# Windows - 在服务中启动 MySQL
\`\`\`

**步骤 3**: 创建数据库和用户

\`\`\`bash
# 连接 MySQL
mysql -u root -p

# 执行以下 SQL 命令
CREATE DATABASE article_publish_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'article_user'@'localhost' IDENTIFIED BY 'article_password_123';
GRANT ALL PRIVILEGES ON article_publish_db.* TO 'article_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
\`\`\`

**步骤 4**: 导入初始化脚本

\`\`\`bash
mysql -u article_user -p article_publish_db < sql/init.sql
# 输入密码: article_password_123
\`\`\`

### 3. 配置应用

编辑 `src/main/resources/application.yml`:

\`\`\`yaml
spring:
  application:
    name: article-publish-platform
  
  datasource:
    url: jdbc:mysql://localhost:3306/article_publish_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=UTC
    username: article_user
    password: article_password_123
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
  
  jackson:
    time-zone: UTC
    serialization:
      write-dates-as-timestamps: false

# JWT 配置
jwt:
  secret: your-secret-key-change-this-in-production-min-32-chars
  expiration: 86400000

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api

# 日志配置
logging:
  level:
    root: INFO
    com.articlehub: DEBUG
\`\`\`

**重要**: 修改 JWT secret 和数据库密码为强密码！

### 4. 检查依赖

检查 `pom.xml` 中的依赖版本是否与你的环境兼容。

主要依赖：
\`\`\`xml
<java.version>17</java.version>
<spring-boot.version>3.2.0</spring-boot.version>
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
\`\`\`

### 5. 构建项目

\`\`\`bash
# 清理并构建
mvn clean package

# 或仅编译（不打包）
mvn clean compile
\`\`\`

首次构建会下载所有依赖，可能耗时较长。

### 6. 运行应用

#### 方式 1: 使用 Maven 插件（开发环境推荐）

\`\`\`bash
mvn spring-boot:run
\`\`\`

#### 方式 2: 运行 JAR 文件（生产环境推荐）

\`\`\`bash
# 首先构建 JAR
mvn clean package

# 然后运行
java -jar target/article-publish-platform-1.0.0.jar
\`\`\`

#### 方式 3: 在 IDE 中运行

如果使用 IntelliJ IDEA 或 Eclipse：

1. 打开项目
2. 找到 `ArticlePublishApplication` 类
3. 右键选择 "Run" 或 "Debug"

### 7. 验证应用

应用启动成功后，访问以下端点验证：

\`\`\`bash
# 获取健康状态
curl http://localhost:8080/api/actuator/health

# 如果返回 {"status":"UP"}，表示应用运行正常
\`\`\`

## 📚 API 文档

应用启动后，可通过以下方式访问 API 文档：

### Swagger UI（推荐）

\`\`\`
http://localhost:8080/api/swagger-ui.html
\`\`\`

### API 端点

#### 认证

\`\`\`bash
# 用户注册
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secure_password_123"
}

# 用户登录
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secure_password_123"
}

# 响应示例
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "123",
    "username": "john_doe",
    "email": "john@example.com"
  }
}
\`\`\`

#### 文章管理

\`\`\`bash
# 创建文章
POST /api/articles
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "我的第一篇文章",
  "content": "这是文章内容",
  "markdown": "# 标题\n文章内容",
  "theme": "default",
  "status": "draft"
}

# 获取所有文章
GET /api/articles
Authorization: Bearer <token>

# 获取单篇文章
GET /api/articles/{id}
Authorization: Bearer <token>

# 更新文章
PUT /api/articles/{id}
Authorization: Bearer <token>

# 删除文章
DELETE /api/articles/{id}
Authorization: Bearer <token>
\`\`\`

#### 一键发布

\`\`\`bash
# 发布到多个平台
POST /api/publish/publish-article
Authorization: Bearer <token>
Content-Type: application/json

{
  "articleId": "123",
  "platforms": ["wechat", "juejin", "cnblogs", "csdn", "zhihu"]
}

# 响应示例
{
  "articleId": "123",
  "results": [
    {
      "platform": "juejin",
      "success": true,
      "publicUrl": "https://juejin.cn/post/123456"
    },
    {
      "platform": "cnblogs",
      "success": true,
      "publicUrl": "https://www.cnblogs.com/article/123"
    }
  ],
  "timestamp": "2025-01-01T10:30:00Z"
}
\`\`\`

## 🔧 常见问题

### Q: 启动时出现数据库连接错误

**A**: 
1. 检查 MySQL 是否运行：`mysql -u root -p`
2. 验证用户名和密码是否正确
3. 确认数据库已创建：`SHOW DATABASES;`
4. 检查数据库初始化脚本是否执行成功

### Q: 出现 "Port 8080 already in use" 错误

**A**: 
\`\`\`bash
# 查找占用端口的进程
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# 修改配置文件中的端口
server:
  port: 8081  # 修改为其他端口
\`\`\`

### Q: JWT token 过期问题

**A**: 修改 `application.yml` 中的过期时间：
\`\`\`yaml
jwt:
  expiration: 604800000  # 7天（毫秒）
\`\`\`

### Q: 构建时出现 "Java version mismatch" 错误

**A**: 确保安装的 Java 版本是 17 或更高：
\`\`\`bash
java -version
javac -version
\`\`\`

### Q: 平台发布失败

**A**:
1. 确认平台账户 Token 正确
2. 查看后端日志了解具体错误
3. 检查网络连接

## 📊 数据库结构

### 主要表

\`\`\`
users                 # 用户表
├── id (主键)
├── username (唯一)
├── email (唯一)
├── password
├── avatar
└── created_at

articles              # 文章表
├── id (主键)
├── user_id (外键)
├── title
├── content
├── markdown
├── theme
├── status (draft/published/archived)
├── created_at
└── updated_at

platform_accounts     # 平台账户表
├── id (主键)
├── user_id (外键)
├── platform (唯一组合键)
├── account_name
├── token
├── is_connected
└── created_at

platform_publish      # 发布记录表
├── id (主键)
├── article_id (外键)
├── platform
├── public_url
├── status
├── error_message
└── published_at
\`\`\`

## 🌐 跨域配置

应用已配置 CORS，允许来自 `http://localhost:5173` 的请求。

如需修改，编辑 `src/main/java/com/articlehub/config/CorsConfig.java`

## 🔐 安全建议

1. **修改 JWT Secret**
   \`\`\`yaml
   jwt:
     secret: your-very-long-and-secure-secret-key-min-32-chars
   \`\`\`

2. **修改数据库密码**
   \`\`\`bash
   ALTER USER 'article_user'@'localhost' IDENTIFIED BY 'new_strong_password';
   \`\`\`

3. **启用 HTTPS**（生产环境）

4. **定期备份数据库**
   \`\`\`bash
   mysqldump -u article_user -p article_publish_db > backup.sql
   \`\`\`

## 📈 性能优化

### 索引

数据库已预先创建必要的索引，包括：
- username 唯一索引
- email 唯一索引
- user_id 外键索引
- platform 组合索引

### 缓存

可在 `application.yml` 中启用 Redis 缓存：

\`\`\`yaml
spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
\`\`\`

## 🚀 部署到生产环境

### 使用 Docker

\`\`\`bash
# 构建 Docker 镜像
docker build -t article-publish-platform .

# 运行容器
docker run -d \
  --name article-app \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://mysql-server:3306/article_publish_db \
  -e DB_USERNAME=article_user \
  -e DB_PASSWORD=your_password \
  article-publish-platform
\`\`\`

### 使用 Linux 服务

创建 `/etc/systemd/system/article-publish.service`:

\`\`\`ini
[Unit]
Description=Article Publish Platform
After=network.target

[Service]
Type=simple
User=app
WorkingDirectory=/opt/article-publish
ExecStart=/usr/bin/java -jar /opt/article-publish/app.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
\`\`\`

启动服务：
\`\`\`bash
sudo systemctl start article-publish
sudo systemctl enable article-publish
\`\`\`

## 📝 日志

查看应用日志：

\`\`\`bash
# 显示最后 100 行日志
tail -100f logs/application.log

# 查找错误
grep ERROR logs/application.log
\`\`\`

## 🔄 升级指南

1. 备份数据库
2. 停止应用
3. 拉取最新代码
4. 运行 `mvn clean package`
5. 重启应用

## 📞 技术支持

- 查看官方文档：https://spring.io/projects/spring-boot
- 提交 Issue：GitHub 项目页面
- 联系开发团队

---

**版本**: 1.0.0  
**最后更新**: 2025年1月  
**兼容 Spring Boot**: 3.2.0+
