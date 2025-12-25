package com.example.englishwords.controller;

import com.example.englishwords.entity.User;
import com.example.englishwords.service.UserService;
import com.example.englishwords.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        if (userService.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        User registeredUser = userService.register(user);
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("data", registeredUser);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        // 添加日志以便调试
        System.out.println("=== LOGIN REQUEST RECEIVED ===");
        System.out.println("Received login request: " + credentials);
        
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // 添加更多调试信息
        System.out.println("Attempting to login user: " + username);
        
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userService.login(username, password);
        
        System.out.println("User found: " + userOptional.isPresent());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("User details - ID: " + user.getId() + ", Username: " + user.getUsername());
            String token = jwtUtil.generateToken(user.getUsername());
            System.out.println("Generated token: " + token);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("nickName", user.getNickName());
            userData.put("totalScore", user.getTotalScore());
            userData.put("role", user.getRole());
            userData.put("grade", user.getGrade());
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", userData);
            response.put("token", token);
            System.out.println("=== LOGIN SUCCESSFUL ===");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Login failed for user: " + username);
            response.put("success", false);
            
            // 登录失败时，尝试查询该用户信息，返回其年级信息
            Optional<User> userExists = userService.getUserByUsername(username);
            if (userExists.isPresent()) {
                User user = userExists.get();
                String gradeInfo = user.getGrade() != null ? user.getGrade() + "年级" : "未选择年级";
                response.put("message", "用户名或密码错误（您注册的是" + gradeInfo + ")");
                response.put("grade", user.getGrade());
            } else {
                response.put("message", "用户名或密码错误");
            }
            
            System.out.println("=== LOGIN FAILED ===");
            return ResponseEntity.status(401).body(response);
        }
    }
    
    @GetMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword() {
        Map<String, Object> response = new HashMap<>();
        
        String rawPassword = "admin123";
        // 使用SM4PasswordEncoder加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // 验证密码匹配
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        
        response.put("rawPassword", rawPassword);
        response.put("encodedPassword", encodedPassword);
        response.put("matches", matches);
        
        return ResponseEntity.ok(response);
    }
}