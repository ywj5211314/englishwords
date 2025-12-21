# 后端项目配置说明

## 项目概述

英语单词考试系统的后端部分，基于Spring Boot构建，提供了RESTful API接口用于用户管理、单词学习、考试答题、积分奖励等功能。

## 技术栈

- Java 8
- Spring Boot 2.7.15
- Spring Security (JWT认证)
- Spring Data JPA
- MySQL 8.0+
- Maven 3.6+
- Lombok 1.18.30

## 项目结构

```
src/main/java/com/example/englishwords/
├── EnglishwordsApplication.java     # 启动类
├── config/                          # 配置类
│   ├── SecurityConfig.java          # Spring Security配置
│   └── WebConfig.java              # Web MVC配置
├── controller/                      # 控制器层
│   ├── UserController.java          # 用户相关接口
│   ├── WordController.java          # 单词相关接口
│   ├── ExamController.java          # 考试相关接口
│   └── AdminController.java         # 管理员相关接口
├── entity/                          # 实体类
│   ├── User.java                    # 用户实体
│   ├── Word.java                    # 单词实体
│   ├── ExamRecord.java              # 考试记录实体
│   └── WrongWord.java               # 错题实体
├── filter/                          # 过滤器
│   └── JwtAuthenticationFilter.java # JWT认证过滤器
├── repository/                      # 数据访问层
│   ├── UserRepository.java          # 用户数据访问
│   ├── WordRepository.java          # 单词数据访问
│   ├── ExamRecordRepository.java    # 考试记录数据访问
│   └── WrongWordRepository.java     # 错题数据访问
├── service/                         # 业务逻辑层
│   ├── UserService.java             # 用户业务逻辑
│   ├── WordService.java             # 单词业务逻辑
│   ├── ExamService.java             # 考试业务逻辑
│   └── JwtUserDetailsService.java   # JWT用户详情服务
└── util/                           # 工具类
    └── JwtUtil.java                 # JWT工具类
```

## 核心配置

### 1. 应用配置 (application.properties)

```properties
# 应用基本信息
spring.application.name=englishwords
server.port=8087

# MySQL数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/englishwords?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# 连接池配置
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=12
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

### 2. 安全配置 (SecurityConfig.java)

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

### 3. CORS配置 (WebConfig.java)

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
```

## 核心功能模块

### 1. 用户管理模块

#### 实体类 (User.java)
```java
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String nickName;
    
    private Integer totalScore = 0;
    
    private String role = "USER";
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

#### 控制器 (UserController.java)
```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        // 注册逻辑
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        // 登录逻辑
    }
}
```

### 2. JWT认证模块

#### JWT工具类 (JwtUtil.java)
```java
@Component
public class JwtUtil {
    
    private Key secretKey = Keys.hmacShaKeyFor("mySecretKeyForEnglishWordsApp1234567890".getBytes());
    private long expirationTime = 86400000; // 24小时
    
    public String generateToken(String username) {
        // 生成token
    }
    
    public String getUsernameFromToken(String token) {
        // 从token获取用户名
    }
    
    public boolean validateToken(String token) {
        // 验证token
    }
    
    public boolean isTokenExpired(String token) {
        // 检查token是否过期
    }
}
```

#### 认证过滤器 (JwtAuthenticationFilter.java)
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 跳过不需要认证的公共接口
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/user/register") || requestURI.equals("/api/user/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // JWT认证逻辑
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 设置认证信息
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## 数据库设计

### 1. 表结构 (schema.sql)

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

### 2. 初始数据 (data.sql)

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

## API接口文档

### 用户相关接口

#### 注册用户
- **URL**: `POST /api/user/register`
- **参数**: 
  ```json
  {
    "username": "testuser",
    "password": "testpass123",
    "nickName": "测试用户"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "message": "注册成功",
    "data": {
      "id": 1,
      "username": "testuser",
      "nickName": "测试用户",
      "totalScore": 0,
      "role": "USER"
    }
  }
  ```

#### 用户登录
- **URL**: `POST /api/user/login`
- **参数**: 
  ```json
  {
    "username": "testuser",
    "password": "testpass123"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "message": "登录成功",
    "data": {
      "id": 1,
      "username": "testuser",
      "nickName": "测试用户",
      "totalScore": 0,
      "role": "USER"
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

## 运行和部署

### 开发环境运行

```bash
# 进入项目根目录
cd englishwords

# 启动Spring Boot应用
mvn spring-boot:run
```

### 生产环境打包

```bash
# 打包成可执行JAR
mvn clean package

# 运行JAR文件
java -jar target/englishwords-0.0.1-SNAPSHOT.jar
```

## 注意事项

1. 确保MySQL数据库已启动并正确配置
2. 首次运行前需要执行数据库初始化脚本
3. JWT密钥在JwtUtil中配置，生产环境应使用环境变量
4. CORS配置允许前端域名访问
5. 密码使用BCrypt加密存储
6. 使用了Lombok简化实体类代码
7. 所有API接口都有统一的响应格式