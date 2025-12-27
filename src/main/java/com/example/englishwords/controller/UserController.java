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
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userService.login(username, password);
        // 角色为学生，选择年级不能为空
        if (userOptional.get().getRole().equals("STUDENT") && null == credentials.get("grade")) {
            response.put("message", "年级不能为空！");
            return ResponseEntity.ok(response);
        }

        // 登录时，尝试查询该用户信息，返回其年级信息
        Optional<User> userExists = userService.getUserByUsername(username);
        if (!userOptional.get().getRole().equals("ADMIN")&& !userOptional.get().getRole().equals("TEACHER")){
            if (userExists.isPresent() && Integer.parseInt(credentials.get("grade")) != userExists.get().getGrade()) {
                User user1 = userExists.get();
                String gradeInfo = user1.getGrade() != null ? user1.getGrade() + "年级" : "未选择年级";
                response.put("message", "用户名或密码错误（您注册的是" + gradeInfo + ")");
                response.put("grade", user1.getGrade());
                return ResponseEntity.ok(response);
            }
        }


        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtUtil.generateToken(user.getUsername());

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
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
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