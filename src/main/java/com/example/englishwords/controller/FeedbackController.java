package com.example.englishwords.controller;

import com.example.englishwords.entity.Word;
import com.example.englishwords.entity.WordFeedback;
import com.example.englishwords.service.WordFeedbackService;
import com.example.englishwords.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    
    @Autowired
    private WordFeedbackService feedbackService;
    
    @Autowired
    private WordService wordService;
    
    /**
     * 学生提交反馈
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody WordFeedback feedback) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            WordFeedback saved = feedbackService.createFeedback(feedback);
            response.put("success", true);
            response.put("message", "反馈提交成功，感谢您的反馈！");
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "反馈提交失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取所有反馈（管理员）
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllFeedbacks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WordFeedback> feedbacks = feedbackService.getAllFeedbacks();
            response.put("success", true);
            response.put("data", feedbacks);
            response.put("count", feedbacks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取反馈列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取待处理的反馈（管理员）
     */
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingFeedbacks() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WordFeedback> feedbacks = feedbackService.getPendingFeedbacks();
            response.put("success", true);
            response.put("data", feedbacks);
            response.put("count", feedbacks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取反馈列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取待处理反馈数量
     */
    @GetMapping("/pending-count")
    public ResponseEntity<Map<String, Object>> getPendingCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long count = feedbackService.countPendingFeedbacks();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取数量失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 处理反馈 - 修改单词并标记为已解决
     */
    @PostMapping("/resolve/{id}")
    public ResponseEntity<Map<String, Object>> resolveFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminRemark = (String) request.get("adminRemark");
            Map<String, Object> wordUpdate = (Map<String, Object>) request.get("wordUpdate");
            
            // 获取反馈信息
            Optional<WordFeedback> optionalFeedback = feedbackService.getFeedbackById(id);
            if (!optionalFeedback.isPresent()) {
                response.put("success", false);
                response.put("message", "反馈不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            WordFeedback feedback = optionalFeedback.get();
            
            // 如果需要更新单词
            if (wordUpdate != null && wordUpdate.containsKey("english") && wordUpdate.containsKey("chinese")) {
                Optional<Word> optionalWord = wordService.getWordById(feedback.getWordId());
                if (optionalWord.isPresent()) {
                    Word word = optionalWord.get();
                    word.setEnglish((String) wordUpdate.get("english"));
                    word.setChinese((String) wordUpdate.get("chinese"));
                    wordService.updateWord(word.getId(), word);
                }
            }
            
            // 标记反馈为已解决
            WordFeedback resolved = feedbackService.resolveFeedback(id, adminRemark);
            
            response.put("success", true);
            response.put("message", "反馈已处理");
            response.put("data", resolved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 拒绝反馈
     */
    @PostMapping("/reject/{id}")
    public ResponseEntity<Map<String, Object>> rejectFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminRemark = request.get("adminRemark");
            WordFeedback rejected = feedbackService.rejectFeedback(id, adminRemark);
            
            if (rejected != null) {
                response.put("success", true);
                response.put("message", "反馈已拒绝");
                response.put("data", rejected);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "反馈不存在");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除反馈
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFeedback(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            feedbackService.deleteFeedback(id);
            response.put("success", true);
            response.put("message", "反馈已删除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
