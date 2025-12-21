# SM4加密迁移指南

本文档说明如何将系统中的BCrypt密码加密替换为SM4加密。

## 1. 依赖更新

已在`pom.xml`中添加了Bouncy Castle依赖，用于支持SM4算法：

```xml
<!-- Bouncy Castle for SM4 encryption -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
```

## 2. 新增类

### 2.1 SM4Util工具类

路径：`src/main/java/com/example/englishwords/util/SM4Util.java`

该类提供了SM4加密和解密的基本功能。

### 2.2 SM4PasswordEncoder

路径：`src/main/java/com/example/englishwords/util/SM4PasswordEncoder.java`

该类实现了Spring Security的PasswordEncoder接口，用于集成到现有安全框架中。

## 3. 配置更新

### 3.1 SecurityConfig更新

在`SecurityConfig.java`中，已将PasswordEncoder Bean从BCryptPasswordEncoder更改为SM4PasswordEncoder：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new com.example.englishwords.util.SM4PasswordEncoder();
}
```

## 4. 数据库密码迁移

### 4.1 初始数据脚本更新

`src/main/resources/db/data.sql`中的初始用户数据已更新为使用SM4加密占位符：

```sql
-- 插入示例用户数据
-- 密码已使用SM4加密: admin123 -> [SM4_ENCRYPTED_ADMIN_PASSWORD]
-- 密码已使用SM4加密: test123 -> [SM4_ENCRYPTED_TESTUSER_PASSWORD]
INSERT INTO `englishwords`.`user` (`username`, `password`, `nick_name`, `total_score`, `role`, `grade`, `created_at`, `updated_at`) VALUES
('admin', '[SM4_ENCRYPTED_ADMIN_PASSWORD]', '管理员', 0, 'ADMIN', NULL, NOW(), NOW()),
('testuser', '[SM4_ENCRYPTED_TESTUSER_PASSWORD]', '测试用户', 0, 'USER', 1, NOW(), NOW());
```

### 4.2 密码更新脚本

提供了`update_passwords.sql`脚本用于更新现有数据库中的密码。

## 5. 测试

### 5.1 测试端点更新

`UserController`中的`/test-password`端点已更新为使用SM4PasswordEncoder进行测试。

### 5.2 测试类

提供了`TestSM4.java`用于验证SM4加密功能。

## 6. 迁移步骤

1. 编译项目以确保所有代码正确无误
2. 运行`TestSM4.java`生成SM4加密的密码值
3. 更新`data.sql`文件中的占位符为实际的SM4加密值
4. 如果数据库中已有用户数据，使用`update_passwords.sql`脚本更新密码
5. 重新启动应用程序
6. 测试登录和注册功能

## 7. 注意事项

1. SM4是一种对称加密算法，需要妥善保管密钥
2. 当前实现使用固定密钥，生产环境中应考虑使用更安全的密钥管理方案
3. 由于加密算法变更，原有使用BCrypt加密的密码将无法验证，需要进行迁移
4. 建议在低峰期进行密码迁移，以减少对用户的影响