package com.example.englishwords.controller;

import com.example.englishwords.entity.User;
import com.example.englishwords.entity.Word;
import com.example.englishwords.service.UserService;
import com.example.englishwords.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // 获取所有用户
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
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