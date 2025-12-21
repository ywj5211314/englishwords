package com.example.englishwords.controller;

import com.example.englishwords.entity.UserWordError;
import com.example.englishwords.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    
    @Autowired
    private ExamService examService;
    
    @GetMapping("/user/errors/{userId}")
    public ResponseEntity<Map<String, Object>> getUserErrors(@PathVariable Long userId) {
        List<UserWordError> errors = examService.getUserWordErrors(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", errors);
        return ResponseEntity.ok(response);
    }
}