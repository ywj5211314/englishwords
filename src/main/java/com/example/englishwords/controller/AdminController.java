package com.example.englishwords.controller;

import com.example.englishwords.entity.User;
import com.example.englishwords.entity.Word;
import com.example.englishwords.service.UserService;
import com.example.englishwords.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private WordService wordService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 获取所有用户
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
        return ResponseEntity.ok(response);
    }
    
    // 添加新用户
    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户名是否已存在
        if (userService.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查必要字段
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("success", false);
            response.put("message", "密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 设置默认角色
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }
        
        // 注册用户（密码会被加密）
        User createdUser = userService.register(user);
        response.put("success", true);
        response.put("message", "用户创建成功");
        response.put("data", createdUser);
        return ResponseEntity.ok(response);
    }
    
    // 编辑用户信息
    @PutMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOptional = userService.getUserById(id);
        if (!userOptional.isPresent()) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        
        // 更新昵称
        if (updateData.containsKey("nickName")) {
            user.setNickName((String) updateData.get("nickName"));
        }
        
        // 更新角色
        if (updateData.containsKey("role")) {
            user.setRole((String) updateData.get("role"));
        }
        
        // 更新年级
        if (updateData.containsKey("grade")) {
            Object gradeObj = updateData.get("grade");
            if (gradeObj != null) {
                if (gradeObj instanceof Integer) {
                    user.setGrade((Integer) gradeObj);
                } else if (gradeObj instanceof String) {
                    try {
                        user.setGrade(Integer.parseInt((String) gradeObj));
                    } catch (NumberFormatException e) {
                        user.setGrade(null);
                    }
                }
            }
        }
        
        // 更新密码（如果提供了新密码）
        if (updateData.containsKey("password")) {
            String newPassword = (String) updateData.get("password");
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }
        }
        
        User updatedUser = userService.updateUser(user);
        response.put("success", true);
        response.put("message", "用户更新成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }
    
    // 重置用户密码
    @PutMapping("/user/{id}/password")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        Map<String, Object> response = new HashMap<>();
        
        String newPassword = passwordData.get("password");
        if (newPassword == null || newPassword.isEmpty()) {
            response.put("success", false);
            response.put("message", "新密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<User> userOptional = userService.getUserById(id);
        if (!userOptional.isPresent()) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userService.updateUser(user);
        
        response.put("success", true);
        response.put("message", "密码重置成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }

    // 删除用户
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "用户删除成功");
        } else {
            response.put("success", false);
            response.put("message", "用户删除失败");
        }
        return ResponseEntity.ok(response);
    }

    // 设置用户年级
    @PutMapping("/user/{id}/grade")
    public ResponseEntity<Map<String, Object>> setUserGrade(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer grade = payload.get("grade");
        Optional<User> userOptional = userService.getUserById(id);
        
        if (!userOptional.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        user.setGrade(grade);
        User updatedUser = userService.updateUser(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户年级设置成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }

    // 添加单词
    @PostMapping("/word")
    public ResponseEntity<Map<String, Object>> addWord(@RequestBody Word word) {
        Word savedWord = wordService.addWord(word);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedWord);
        return ResponseEntity.ok(response);
    }

    // 更新单词
    @PutMapping("/word/{id}")
    public ResponseEntity<Map<String, Object>> updateWord(@PathVariable Long id, @RequestBody Word word) {
        Word updatedWord = wordService.updateWord(id, word);
        Map<String, Object> response = new HashMap<>();
        if (updatedWord != null) {
            response.put("success", true);
            response.put("data", updatedWord);
        } else {
            response.put("success", false);
            response.put("message", "单词更新失败");
        }
        return ResponseEntity.ok(response);
    }

    // 删除单词
    @DeleteMapping("/word/{id}")
    public ResponseEntity<Map<String, Object>> deleteWord(@PathVariable Long id) {
        boolean deleted = wordService.deleteWord(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "单词删除成功");
        } else {
            response.put("success", false);
            response.put("message", "单词删除失败");
        }
        return ResponseEntity.ok(response);
    }
}