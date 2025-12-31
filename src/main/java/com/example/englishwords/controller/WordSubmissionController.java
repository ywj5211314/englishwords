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
        Map<String, Object> response = new HashMap<>();
        
        // 校验单词是否已存在
        List<String> duplicateWords = submissionService.validateWordSubmission(submission);
        if (!duplicateWords.isEmpty()) {
            response.put("success", false);
            response.put("message", "以下单词数据库中已存在，不允许提交");
            response.put("duplicateWords", duplicateWords);
            return ResponseEntity.badRequest().body(response);
        }
        
        WordSubmission savedSubmission = submissionService.submitWord(submission);
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
    
    /**
     * 批量批准单词
     */
    @PostMapping("/batch-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> batchApproveSubmissions(
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        java.util.List<Object> idList = (java.util.List<Object>) request.get("ids");
        String adminRemark = (String) request.getOrDefault("adminRemark", "");
        
        // 将Integer或Long转换为Long
        java.util.List<Long> ids = new java.util.ArrayList<>();
        if (idList != null) {
            for (Object id : idList) {
                if (id instanceof Long) {
                    ids.add((Long) id);
                } else if (id instanceof Integer) {
                    ids.add(((Integer) id).longValue());
                }
            }
        }
        
        if (ids == null || ids.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请选择至少一个单词");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> result = submissionService.batchApproveSubmissions(ids, adminRemark);
        Map<String, Object> response = new HashMap<>();
        int successCount = (int) result.get("successCount");
        int failCount = (int) result.get("failCount");
        
        response.put("success", true);
        response.put("message", String.format("成功批准 %d 个单词，失败 %d 个", successCount, failCount));
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量拒绝单词
     */
    @PostMapping("/batch-reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> batchRejectSubmissions(
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        java.util.List<Object> idList = (java.util.List<Object>) request.get("ids");
        String adminRemark = (String) request.get("adminRemark");
        
        // 将Integer或Long转换为Long
        java.util.List<Long> ids = new java.util.ArrayList<>();
        if (idList != null) {
            for (Object id : idList) {
                if (id instanceof Long) {
                    ids.add((Long) id);
                } else if (id instanceof Integer) {
                    ids.add(((Integer) id).longValue());
                }
            }
        }
        
        if (ids == null || ids.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请选择至少一个单词");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (adminRemark == null || adminRemark.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请填写拒绝原因");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> result = submissionService.batchRejectSubmissions(ids, adminRemark);
        Map<String, Object> response = new HashMap<>();
        int successCount = (int) result.get("successCount");
        int failCount = (int) result.get("failCount");
        
        response.put("success", true);
        response.put("message", String.format("成功拒绝 %d 个单词，失败 %d 个", successCount, failCount));
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        return ResponseEntity.ok(response);
    }
}
