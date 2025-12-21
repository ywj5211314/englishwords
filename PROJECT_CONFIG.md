# 英语单词考试系统 - 项目配置说明

## 项目概述

这是一个基于Spring Boot + Vue的英语单词考试系统，具有用户注册登录、单词学习、考试答题、积分奖励等功能。

## 后端配置 (Spring Boot)

### 技术栈
- Java 8
- Spring Boot 2.7.15
- Spring Security (JWT认证)
- Spring Data JPA
- MySQL 8.0+
- Maven 3.6+

### 项目结构
```
src/main/java/com/example/englishwords/
├── EnglishwordsApplication.java     # 启动类
├── config/                          # 配置类
│   ├── SecurityConfig.java          # Spring Security配置
│   └── WebConfig.java              # Web MVC配置
├── controller/                      # 控制器层
├── entity/                          # 实体类
├── filter/                          # 过滤器
├── repository/                      # 数据访问层
├── service/                         # 业务逻辑层
└── util/                           # 工具类
```

### 核心配置文件

#### application.properties
```properties
spring.application.name=englishwords
server.port=8087

# MySQL Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/englishwords?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

# JPA configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# MySQL configuration
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=12
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

#### WebConfig.java (CORS配置)
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
```

#### SecurityConfig.java (安全配置)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()  // 添加CORS支持
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests(authz -> authz
                .antMatchers("/api/user/register", "/api/user/login").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 数据库初始化脚本

#### schema.sql (表结构)
```sql
-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nick_name VARCHAR(100),
    total_score INT DEFAULT 0,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 单词表
CREATE TABLE IF NOT EXISTS word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    english VARCHAR(100) NOT NULL,
    chinese VARCHAR(255) NOT NULL,
    phonetic VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 考试记录表
CREATE TABLE IF NOT EXISTS exam_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    exam_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 错题统计表
CREATE TABLE IF NOT EXISTS wrong_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    wrong_count INT DEFAULT 1,
    last_wrong_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (word_id) REFERENCES word(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_word (user_id, word_id)
);
```

#### data.sql (初始数据)
```sql
-- 插入管理员用户 (密码: admin123)
INSERT INTO user (username, password, nick_name, role) VALUES 
('admin', '$2a$10$rOzJqQZ8QxR5Z8QxR5Z8QeO7.9F6u7.9F6u7.9F6u7.9F6u7.9F6u', '管理员', 'ADMIN');

-- 插入普通用户 (密码: user123)
INSERT INTO user (username, password, nick_name, role) VALUES 
('user1', '$2a$10$u7.9F6u7.9F6u7.9F6u7.O7.9F6u7.9F6u7.9F6u7.9F6u7.9F6u', '用户1', 'USER');

-- 插入一些英语单词
INSERT INTO word (english, chinese, phonetic) VALUES 
('apple', '苹果', '/ˈæpl/'),
('banana', '香蕉', '/bəˈnɑːnə/'),
('computer', '计算机', '/kəmˈpjuːtər/'),
('book', '书', '/bʊk/'),
('water', '水', '/ˈwɔːtər/');

-- 插入考试记录示例
INSERT INTO exam_record (user_id, score) VALUES 
(2, 80),
(2, 90);

-- 插入错题统计示例
INSERT INTO wrong_word (user_id, word_id, wrong_count) VALUES 
(2, 1, 3),
(2, 3, 1);
```

## 前端配置 (Vue 3)

### 技术栈
- Vue 3.2.13
- Vue Router 4.6.4
- Axios 1.13.2
- Element Plus (UI组件库)

### 项目结构
```
english-word-app/
├── src/
│   ├── App.vue                     # 根组件
│   ├── main.js                     # 入口文件
│   ├── router/                     # 路由配置
│   ├── views/                      # 页面组件
│   ├── components/                 # 公共组件
│   └── utils/                      # 工具类
├── public/                         # 静态资源
└── package.json                    # 依赖配置
```

### 核心配置文件

#### package.json (依赖配置)
```json
{
  "name": "english-word-app",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "serve": "vue-cli-service serve",
    "build": "vue-cli-service build",
    "lint": "vue-cli-service lint"
  },
  "dependencies": {
    "axios": "^1.13.2",
    "core-js": "^3.8.3",
    "element-plus": "^2.8.7",
    "vue": "^3.2.13",
    "vue-router": "^4.6.4"
  },
  "devDependencies": {
    "@babel/core": "^7.12.16",
    "@babel/eslint-parser": "^7.12.16",
    "@vue/cli-plugin-babel": "~5.0.0",
    "@vue/cli-plugin-eslint": "~5.0.0",
    "@vue/cli-service": "~5.0.0",
    "eslint": "^7.32.0",
    "eslint-plugin-vue": "^8.0.3"
  }
}
```

#### utils/request.js (Axios配置)
```javascript
import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8087',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 在每个请求头中添加token
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.log('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.log('Response error:', error)
    if (error.response && error.response.status === 401) {
      // token过期或无效，清除本地存储并跳转到登录页
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      window.location.href = '/#/login'
    }
    return Promise.reject(error)
  }
)

export default service
```

## 运行项目

### 后端运行
```bash
# 进入项目根目录
cd englishwords

# 启动Spring Boot应用
mvn spring-boot:run
```

### 前端运行
```bash
# 进入前端项目目录
cd english-word-app

# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

### 默认端口
- 后端API: http://localhost:8087
- 前端页面: http://localhost:8080

## API接口说明

### 用户相关
- POST /api/user/register - 用户注册
- POST /api/user/login - 用户登录

### 单词相关
- GET /api/words/random - 获取随机单词
- GET /api/words/wrong - 获取错题单词

### 考试相关
- POST /api/exam/start - 开始考试
- POST /api/exam/submit - 提交答案
- GET /api/exam/history - 获取考试历史

### 用户相关
- GET /api/user/profile - 获取用户信息
- PUT /api/user/score - 更新用户积分

## 注意事项

1. 确保MySQL数据库已启动并正确配置
2. 首次运行前需要执行数据库初始化脚本
3. 前后端需要分别启动才能正常通信
4. 如果遇到跨域问题，请检查CORS配置
5. 生产环境部署时请注意安全性配置