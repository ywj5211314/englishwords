package com.example.englishwords.controller;

import com.example.englishwords.entity.WordSubmission;
import com.example.englishwords.service.WordSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/word-submission")
public class WordSubmissionController {
    
    @Autowired
    private WordSubmissionService submissionService;
    
    /**
     * 老师提交单词
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> submitWord(@RequestBody WordSubmission submission) {
        WordSubmission savedSubmission = submissionService.submitWord(submission);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "单词提交成功，等待管理员审批");
        response.put("data", savedSubmission);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 老师查看自己的提交记录
     */
    @GetMapping("/my-submissions/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getMySubmissions(@PathVariable Long teacherId) {
        List<WordSubmission> submissions = submissionService.getSubmissionsByTeacherId(teacherId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", submissions);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 管理员获取待审批的单词
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPendingSubmissions() {
        List<WordSubmission> submissions = submissionService.getPendingSubmissions();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", submissions);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 管理员批准单词
     */
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approveSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String adminRemark = request.getOrDefault("adminRemark", "");
        WordSubmission submission = submissionService.approveSubmission(id, adminRemark);
        Map<String, Object> response = new HashMap<>();
        if (submission != null) {
            response.put("success", true);
            response.put("message", "单词已批准并添加到数据库");
            response.put("data", submission);
        } else {
            response.put("success", false);
            response.put("message", "操作失败");
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 管理员拒绝单词
     */
    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rejectSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String adminRemark = request.get("adminRemark");
        if (adminRemark == null || adminRemark.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请填写拒绝原因");
            return ResponseEntity.badRequest().body(response);
        }
        
        WordSubmission submission = submissionService.rejectSubmission(id, adminRemark);
        Map<String, Object> response = new HashMap<>();
        if (submission != null) {
            response.put("success", true);
            response.put("message", "已拒绝该单词提交");
            response.put("data", submission);
        } else {
            response.put("success", false);
            response.put("message", "操作失败");
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 统计待审批数量
     */
    @GetMapping("/pending-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPendingCount() {
        long count = submissionService.countPendingSubmissions();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
