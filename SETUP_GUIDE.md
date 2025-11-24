# 完整项目安装和运行指南

这是一个完整的多平台文章发布系统，包含前端和后端。本指南将帮助你从零开始搭建和运行整个项目。

## 📦 项目结构

\`\`\`
article-publish-platform/
├── frontend/                 # Vue 3 前端项目
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   └── README.md
├── backend/                  # Java Spring Boot 后端项目
│   ├── src/
│   ├── pom.xml
│   ├── sql/
│   └── README.md
└── SETUP_GUIDE.md           # 本文件
\`\`\`

## 🎯 安装步骤

### 第一步：系统要求检查

#### 前端需求
- Node.js >= 18.0.0
- npm >= 9.0.0 (或 pnpm/yarn)

#### 后端需求
- Java JDK >= 17
- MySQL >= 8.0
- Maven >= 3.8.0

#### 验证环境

\`\`\`bash
# 检查 Node.js 和 npm
node --version    # 应显示 v18 或更高
npm --version     # 应显示 9 或更高

# 检查 Java
java -version     # 应显示 Java 17 或更高

# 检查 Maven
mvn --version     # 应显示 3.8.0 或更高

# 检查 MySQL
mysql --version   # 应显示 8.0 或更高
\`\`\`

### 第二步：后端配置和启动

#### 2.1 数据库准备

\`\`\`bash
# 启动 MySQL 服务
# macOS: brew services start mysql
# Linux: sudo service mysql start
# Windows: 在服务中启动 MySQL

# 创建数据库
mysql -u root -p
# 输入 root 密码后执行：
CREATE DATABASE article_publish_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'article_user'@'localhost' IDENTIFIED BY 'article_password_123';
GRANT ALL PRIVILEGES ON article_publish_db.* TO 'article_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# 导入初始化脚本
mysql -u article_user -p article_publish_db < backend/sql/init.sql
# 输入密码: article_password_123
\`\`\`

#### 2.2 后端构建和启动

\`\`\`bash
cd backend

# 清理并构建
mvn clean package

# 或直接运行（开发模式）
mvn spring-boot:run
\`\`\`

后端启动成功后，应该看到类似输出：
\`\`\`
Tomcat started on port(s): 8080 (http)
Started ArticlePublishApplication in X.XXX seconds
\`\`\`

#### 2.3 验证后端

\`\`\`bash
# 在另一个终端中测试
curl http://localhost:8080/api/actuator/health
# 应返回: {"status":"UP"}
\`\`\`

### 第三步：前端配置和启动

#### 3.1 安装依赖

\`\`\`bash
cd frontend

# 使用 npm
npm install

# 或使用 pnpm (推荐)
pnpm install
\`\`\`

#### 3.2 启动开发服务器

\`\`\`bash
# 使用 npm
npm run dev

# 或使用 pnpm
pnpm dev
\`\`\`

前端启动成功后，应该看到：
\`\`\`
➜ Local: http://localhost:5173
\`\`\`

#### 3.3 访问应用

打开浏览器访问：http://localhost:5173

## 🔐 首次使用

### 创建账户

1. 点击"注册新账户"
2. 输入用户名、邮箱和密码
3. 点击"注册"按钮

### 登录

1. 输入注册的用户名和密码
2. 点击"登录"按钮

### 创建第一篇文章

1. 从导航菜单选择"编辑文章"
2. 输入文章标题
3. 在左侧编辑器输入 Markdown 内容
4. 查看右侧实时预览
5. 点击"保存草稿"

### 配置发布平台

1. 从导航菜单选择"平台配置"
2. 选择要连接的平台
3. 输入平台账户名和 API Token
4. 点击"保存"

### 一键发布

1. 从"文章管理"选择要发布的文章
2. 选择目标平台
3. 点击"发布"按钮

## 🛑 停止应用

### 停止后端

在后端终端按 `Ctrl+C`

### 停止前端

在前端终端按 `Ctrl+C`

### 停止数据库

\`\`\`bash
# macOS
brew services stop mysql

# Linux
sudo service mysql stop

# Windows
在服务中停止 MySQL
\`\`\`

## 🔧 故障排查

### 问题：前后端无法通信

**解决方案**：
- 确保后端运行在 `http://localhost:8080`
- 确保前端运行在 `http://localhost:5173`
- 检查浏览器控制台是否有 CORS 错误

### 问题：数据库连接失败

**解决方案**：
\`\`\`bash
# 验证 MySQL 运行状态
mysql -u article_user -p -e "SELECT 1"

# 检查数据库是否存在
mysql -u article_user -p -e "SHOW DATABASES;"

# 检查 application.yml 中的数据库配置
\`\`\`

### 问题：Java 版本不兼容

**解决方案**：
\`\`\`bash
# 安装正确的 Java 版本
# 从 https://www.oracle.com/java/technologies/downloads/#java17 下载

# 设置 JAVA_HOME 环境变量
# 验证版本
java -version
\`\`\`

### 问题：Node.js 版本过低

**解决方案**：
\`\`\`bash
# 升级 Node.js
# 从 https://nodejs.org/ 下载最新版本

# 或使用版本管理器
# nvm install 20
# nvm use 20
\`\`\`

## 📚 使用文档

### 前端文档

详见 `frontend/README.md`

### 后端文档

详见 `backend/INSTALL_GUIDE.md` 和 `backend/PLATFORMS_GUIDE.md`

## 🚀 生产环境部署

### 前端部署

\`\`\`bash
cd frontend

# 构建生产版本
npm run build

# 部署 dist 文件夹到 Web 服务器或 CDN
\`\`\`

### 后端部署

\`\`\`bash
cd backend

# 构建 JAR 文件
mvn clean package -DskipTests

# 在服务器上运行
java -jar target/article-publish-platform-1.0.0.jar \
  --spring.datasource.url=jdbc:mysql://db-host:3306/article_publish_db \
  --spring.datasource.username=article_user \
  --spring.datasource.password=your_password \
  --jwt.secret=your_long_secure_secret_key
\`\`\`

## 📊 16 个支持的发布平台

1. 微信公众号
2. 稀土掘金
3. 博客园
4. 知乎
5. CSDN
6. 今日头条
7. 简书
8. Medium
9. Hashnode
10. Dev.to
11. 开源中国
12. 思否
13. 百家号
14. 小红书
15. 豆瓣
16. 微博

## 💡 关键特性

- ✅ Vue 3 + Element Plus + TypeScript + Pinia
- ✅ Spring Boot 3 + MySQL + JPA
- ✅ JWT 认证
- ✅ Markdown 编辑和预览
- ✅ 4 种主题切换
- ✅ 16 个平台一键发布
- ✅ 响应式设计
- ✅ 深色/浅色模式

## 📞 支持

如遇问题，请：

1. 查看相应的 README 文件
2. 检查日志输出
3. 验证配置文件
4. 查看故障排查部分

## 📄 许可证

MIT

---

**祝你使用愉快！** 🎉
