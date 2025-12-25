package com.example.englishwords.controller;

import com.example.englishwords.entity.Word;
import com.example.englishwords.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/word")
public class WordController {
    
    @Autowired
    private WordService wordService;
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllWords() {
        List<Word> words = wordService.getAllWords();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getWordById(@PathVariable Long id) {
        Optional<Word> word = wordService.getWordById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (word.isPresent()) {
            response.put("success", true);
            response.put("data", word.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "单词不存在");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createWord(@RequestBody Word word) {
        Word savedWord = wordService.saveWord(word);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "单词创建成功");
        response.put("data", savedWord);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateWord(@PathVariable Long id, @RequestBody Word wordDetails) {
        Word updatedWord = wordService.updateWord(id, wordDetails);
        Map<String, Object> response = new HashMap<>();
        
        if (updatedWord != null) {
            response.put("success", true);
            response.put("message", "单词更新成功");
            response.put("data", updatedWord);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "单词不存在");
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "单词删除成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchWords(@RequestParam String keyword) {
        List<Word> words = wordService.searchWords(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/random")
    public ResponseEntity<Map<String, Object>> getRandomWords(@RequestParam(defaultValue = "10") int count) {
        List<Word> words = wordService.getRandomWords(count);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    // 按年级获取单词
    @GetMapping("/grade/{grade}")
    public ResponseEntity<Map<String, Object>> getWordsByGrade(@PathVariable Integer grade) {
        List<Word> words = wordService.getWordsByGrade(grade);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    // 按年级和单元获取单词
    @GetMapping("/grade/{grade}/unit/{unit}")
    public ResponseEntity<Map<String, Object>> getWordsByGradeAndUnit(
            @PathVariable Integer grade, 
            @PathVariable Integer unit) {
        List<Word> words = wordService.getWordsByGradeAndUnit(grade, unit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    // 按年级随机获取单词
    @GetMapping("/grade/{grade}/random")
    public ResponseEntity<Map<String, Object>> getRandomWordsByGrade(
            @PathVariable Integer grade,
            @RequestParam(defaultValue = "10") int count) {
        List<Word> words = wordService.getRandomWordsByGrade(grade, count);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    // 按年级和单元随机获取单词
    @GetMapping("/grade/{grade}/unit/{unit}/random")
    public ResponseEntity<Map<String, Object>> getRandomWordsByGradeAndUnit(
            @PathVariable Integer grade,
            @PathVariable Integer unit,
            @RequestParam(defaultValue = "10") int count) {
        List<Word> words = wordService.getRandomWordsByGradeAndUnit(grade, unit, count);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据用户错题历史创建加权考试题目
     * 优先考察学生的错题，错题次数越多，出现概率越高
     * 
     * 例如：学生错頔3次的单词会出现，和正确的单词平率是，比例为3:1
     */
    @GetMapping("/grade/{grade}/weighted-random")
    public ResponseEntity<Map<String, Object>> getWeightedRandomWordsByGrade(
            @PathVariable Integer grade,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Integer unit,
            @RequestParam(defaultValue = "10") int count) {
        List<Word> words = wordService.getWeightedRandomWordsByGrade(userId, grade, unit, count);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据用户错题历史创建加权考试题目（按年级和单元）
     */
    @GetMapping("/grade/{grade}/unit/{unit}/weighted-random")
    public ResponseEntity<Map<String, Object>> getWeightedRandomWordsByGradeAndUnit(
            @PathVariable Integer grade,
            @PathVariable Integer unit,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int count) {
        List<Word> words = wordService.getWeightedRandomWordsByGrade(userId, grade, unit, count);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", words);
        return ResponseEntity.ok(response);
    }
}