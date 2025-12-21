# 英语单词考试系统

一个基于Spring Boot和Vue的英语单词学习与考试系统，具有用户注册登录、单词学习、考试答题、积分奖励等功能。

## 功能特性

- ✅ 用户注册与登录（JWT认证）
- ✅ 单词学习与浏览
- ✅ 在线考试答题
- ✅ 错题统计与复习
- ✅ 积分奖励机制
- ✅ 管理员后台管理
- ✅ 响应式前端界面

## 技术栈

### 后端 (Spring Boot)
- Java 8
- Spring Boot 2.7.15
- Spring Security (JWT认证)
- Spring Data JPA
- MySQL 8.0+
- Maven 3.6+
- SM4加密算法 (国密标准)

### 前端 (Vue 3)
- Vue 3.2.13
- Vue Router 4.6.4
- Axios 1.13.2
- Element Plus UI组件库

## 项目结构

```
englishwords/
├── src/                        # 后端源代码
│   ├── main/
│   │   ├── java/               # Java源代码
│   │   └── resources/          # 配置文件和资源
│   └── test/                   # 测试代码
├── english-word-app/           # 前端源代码
│   ├── src/                    # Vue源代码
│   └── public/                 # 静态资源
├── docs/                       # 文档
├── scripts/                    # 脚本文件
└── sql/                        # 数据库脚本
```

## 快速开始

### 环境准备

1. **Java 8+** - 后端运行环境
2. **Node.js 14+** - 前端构建环境
3. **MySQL 8.0+** - 数据库
4. **Maven 3.6+** - Java项目构建工具

### 数据库配置

1. 创建数据库：
   ```sql
   CREATE DATABASE englishwords CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 修改 `src/main/resources/application.properties` 中的数据库连接配置：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/englishwords?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 初始化数据库

执行 `src/main/resources/db/schema.sql` 和 `src/main/resources/db/data.sql` 脚本初始化数据库表结构和数据。

### 启动后端服务

```bash
# 进入项目根目录
cd englishwords

# 启动Spring Boot应用
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8087`

### 启动前端服务

```bash
# 进入前端项目目录
cd english-word-app

# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

前端服务默认运行在 `http://localhost:8080`

### 一键启动脚本

- Windows: 运行 `start.bat`
- Linux/macOS: 运行 `start.sh`

## API接口

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录

### 单词相关
- `GET /api/words/random` - 获取随机单词
- `GET /api/words/wrong` - 获取错题单词

### 考试相关
- `POST /api/exam/start` - 开始考试
- `POST /api/exam/submit` - 提交答案
- `GET /api/exam/history` - 获取考试历史

### 用户相关
- `GET /api/user/profile` - 获取用户信息
- `PUT /api/user/score` - 更新用户积分

## 项目配置文档

详细的配置说明请参考：
- [后端配置说明](BACKEND_CONFIG.md)
- [前端配置说明](english-word-app/FRONTEND_CONFIG.md)
- [整体项目配置](PROJECT_CONFIG.md)
- [SM4加密迁移指南](SM4_MIGRATION_GUIDE.md)

## 开发指南

### 后端开发

1. 实体类位于 `src/main/java/com/example/englishwords/entity/`
2. 数据访问层位于 `src/main/java/com/example/englishwords/repository/`
3. 业务逻辑层位于 `src/main/java/com/example/englishwords/service/`
4. 控制器层位于 `src/main/java/com/example/englishwords/controller/`

### 前端开发

1. 页面组件位于 `english-word-app/src/views/`
2. 公共组件位于 `english-word-app/src/components/`
3. 路由配置位于 `english-word-app/src/router/`
4. 工具类位于 `english-word-app/src/utils/`

## 部署说明

### 后端部署

```bash
# 打包成可执行JAR
mvn clean package

# 运行JAR文件
java -jar target/englishwords-0.0.1-SNAPSHOT.jar
```

### 前端部署

```bash
# 构建生产版本
cd english-word-app
npm run build
```

构建后的文件位于 `english-word-app/dist/` 目录下，可部署到Nginx、Apache等Web服务器。

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解更多详情。

## 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。