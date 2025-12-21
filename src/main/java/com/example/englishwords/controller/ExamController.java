package com.example.englishwords.controller;

import com.example.englishwords.entity.ExamRecord;
import com.example.englishwords.entity.UserWordError;
import com.example.englishwords.entity.Word;
import com.example.englishwords.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateExamPaper(@RequestBody Map<String, Integer> request) {
        int count = request.getOrDefault("count", 10);
        List<Word> examWords = examService.generateExamPaper(count);
        
        // 将试卷存储在session中（实际项目中可能需要更复杂的处理）
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", examWords);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitExam(@RequestBody Map<String, Object> request, HttpSession session) {
        Long userId = ((Number) request.get("userId")).longValue();
        Map<String, Object> answersObj = (Map<String, Object>) request.get("answers");
        // Convert Map<String, Object> to Map<Long, String>
        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, Object> entry : answersObj.entrySet()) {
            answers.put(Long.valueOf(entry.getKey()), (String) entry.getValue());
        }
        List<Map<String, Object>> examWordsData = (List<Map<String, Object>>) request.get("examWords");
        
        // 转换examWords数据
        List<Word> examWords = new ArrayList<>();
        for (Map<String, Object> wordData : examWordsData) {
            Word word = new Word();
            word.setId(((Number) wordData.get("id")).longValue());
            word.setEnglish((String) wordData.get("english"));
            word.setChinese((String) wordData.get("chinese"));
            examWords.add(word);
        }
        
        Map<String, Object> result = examService.submitExam(userId, answers, examWords);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/records/{userId}")
    public ResponseEntity<Map<String, Object>> getUserExamRecords(@PathVariable Long userId) {
        List<ExamRecord> records = examService.getUserExamRecords(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/errors/{userId}")
    public ResponseEntity<Map<String, Object>> getUserWordErrors(@PathVariable Long userId) {
        List<UserWordError> errors = examService.getUserWordErrors(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", errors);
        return ResponseEntity.ok(response);
    }
}