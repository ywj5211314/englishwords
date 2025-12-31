package com.example.englishwords.controller;

import com.example.englishwords.entity.Word;
import com.example.englishwords.service.OcrService;
import com.example.englishwords.service.WordService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/word-upload")
public class WordUploadController {
    
    @Autowired
    private OcrService ocrService;
    
    @Autowired
    private WordService wordService;
    
    /**
     * 上传图片并识别其中的单词
     * @param image 图片文件
     * @param grade 年级
     * @param unit 单元
     * @return 识别结果
     */
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("grade") Integer grade,
            @RequestParam("unit") Integer unit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查参数是否完整
            if (grade == null || unit == null) {
                response.put("success", false);
                response.put("message", "年级和单元不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查文件是否存在
            if (image == null || image.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要上传的图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查文件类型
            String contentType = image.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                response.put("success", false);
                response.put("message", "请上传有效的图片文件 (JPEG, PNG, GIF等)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 使用OCR识别图片中的文本
            String extractedText = ocrService.extractTextFromImage(image, "eng+chi_sim");
            
            // 解析识别出的文本，提取英语单词和中文翻译
            List<Word> words = parseWordsFromText(extractedText, grade, unit);
            
            // 保存单词到数据库
            List<Word> savedWords = new ArrayList<>();
            for (Word word : words) {
                Word savedWord = wordService.addWord(word);
                savedWords.add(savedWord);
            }
            
            response.put("success", true);
            response.put("message", "单词上传并识别成功");
            response.put("data", savedWords);
            response.put("extractedText", extractedText);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "图片读取失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (TesseractException e) {
            response.put("success", false);
            response.put("message", "OCR识别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 解析文本中的单词对
     * 支持格式：
     * 1. 英语单词 中文释义
     * 2. *英语单词 中文释义
     * 3. 英语单词（括号内容）中文释义
     * @param text 文本内容
     * @param grade 年级
     * @param unit 单元
     * @return 单词列表
     */
    private List<Word> parseWordsFromText(String text, Integer grade, Integer unit) {
        List<Word> words = new ArrayList<>();
        
        // 按行分割文本
        String[] lines = text.split("\\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // 移除行首的*号和其他特殊字符
            line = line.replaceAll("^[*\\s]+", "");
            
            // 尝试提取英语单词和中文释义
            Word word = extractWordFromLine(line, grade, unit);
            
            if (word != null && isValidWord(word)) {
                words.add(word);
            }
        }
        
        return words;
    }
    
    /**
     * 从单行文本中提取单词和释义
     */
    private Word extractWordFromLine(String line, Integer grade, Integer unit) {
        // 匹配英语单词（可能包含括号内的内容如复数形式）后面跟着中文
        // 格式: 英语单词 中文释义 或 英语单词（说明）中文释义
        
        // 先尝试找到第一个中文字符的位置
        int chineseStart = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isChinese(c)) {
                chineseStart = i;
                break;
            }
        }
        
        if (chineseStart <= 0) {
            return null;
        }
        
        // 提取英语部分（中文之前的内容）
        String englishPart = line.substring(0, chineseStart).trim();
        // 提取中文部分
        String chinesePart = line.substring(chineseStart).trim();
        
        // 清理英语部分 - 只保留主要单词，移除括号内容
        englishPart = englishPart.replaceAll("[（\\(][^）\\)]*[）\\)]", "").trim();
        // 只保留字母、连字符和空格
        englishPart = englishPart.replaceAll("[^a-zA-Z\\s-]", "").trim();
        // 如果有多个空格，只保留一个
        englishPart = englishPart.replaceAll("\\s+", " ");
        
        // 清理中文部分 - 移除括号内的英文说明
        chinesePart = chinesePart.replaceAll("[（\\(][a-zA-Z][^）\\)]*[）\\)]", "").trim();
        // 移除英文字符
        StringBuilder cleanChinese = new StringBuilder();
        for (char c : chinesePart.toCharArray()) {
            if (isChinese(c) || c == '；' || c == '，' || c == '、') {
                cleanChinese.append(c);
            }
        }
        chinesePart = cleanChinese.toString().trim();
        
        if (englishPart.isEmpty() || chinesePart.isEmpty()) {
            return null;
        }
        
        Word word = new Word();
        word.setEnglish(englishPart);
        word.setChinese(chinesePart);
        word.setGrade(grade);
        word.setUnit(unit);
        word.setCreatedAt(LocalDateTime.now());
        word.setUpdatedAt(LocalDateTime.now());
        
        return word;
    }
    
    /**
     * 判断字符是否是中文字符
     */
    private boolean isChinese(char c) {
        // Unicode范围：中文基本区
        return c >= 0x4E00 && c <= 0x9FFF;
    }
    
    /**
     * 验证单词是否有效
     * @param word 单词对象
     * @return 是否有效
     */
    private boolean isValidWord(Word word) {
        return word.getEnglish() != null && !word.getEnglish().isEmpty() &&
               word.getChinese() != null && !word.getChinese().isEmpty() &&
               word.getEnglish().length() > 1 && word.getEnglish().length() < 50 &&
               word.getChinese().length() > 1 && word.getChinese().length() < 50;
    }
    
    /**
     * 只识别不保存 - 用于前端编辑后再保存
     * @param image 图片文件
     * @param grade 年级
     * @param unit 单元
     * @return 识别结果
     */
    @PostMapping("/recognize-only")
    public ResponseEntity<Map<String, Object>> recognizeOnly(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("grade") Integer grade,
            @RequestParam("unit") Integer unit) {
        
        System.out.println("=== RECOGNIZE ONLY REQUEST RECEIVED ===");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (grade == null || unit == null) {
                response.put("success", false);
                response.put("message", "年级和单元不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (image == null || image.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要上传的图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            String contentType = image.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                response.put("success", false);
                response.put("message", "请上传有效的图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 使用OCR识别图片中的文本
            String extractedText = ocrService.extractTextFromImage(image, "eng+chi_sim");
            
            // 解析识别出的文本，提取英语单词和中文翻译
            List<Word> words = parseWordsFromText(extractedText, grade, unit);
            
            // 不保存到数据库，直接返回识别结果
            response.put("success", true);
            response.put("message", "识别完成，请检查并编辑后保存");
            response.put("data", words);
            response.put("extractedText", extractedText);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "图片读取失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (TesseractException e) {
            response.put("success", false);
            response.put("message", "OCR识别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量保存单词
     * @param words 单词列表
     * @return 保存结果
     */
    @PostMapping("/batch-save")
    public ResponseEntity<Map<String, Object>> batchSave(@RequestBody List<Word> words) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (words == null || words.isEmpty()) {
                response.put("success", false);
                response.put("message", "没有需要保存的单词");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Word> savedWords = new ArrayList<>();
            for (Word word : words) {
                // 设置创建和更新时间
                word.setCreatedAt(LocalDateTime.now());
                word.setUpdatedAt(LocalDateTime.now());
                
                // 验证单词是否有效
                if (isValidWord(word)) {
                    Word savedWord = wordService.addWord(word);
                    savedWords.add(savedWord);
                }
            }
            
            response.put("success", true);
            response.put("message", "成功保存 " + savedWords.size() + " 个单词");
            response.put("data", savedWords);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}