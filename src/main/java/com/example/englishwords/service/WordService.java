package com.example.englishwords.service;

import com.example.englishwords.entity.Word;
import com.example.englishwords.repository.WordRepository;
import com.example.englishwords.repository.UserWordErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class WordService {
    
    private static final Logger logger = LoggerFactory.getLogger(WordService.class);
    
    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private UserWordErrorRepository userWordErrorRepository;
    
    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }
    
    public Optional<Word> getWordById(Long id) {
        return wordRepository.findById(id);
    }
    
    public Word saveWord(Word word) {
        word.setCreatedAt(LocalDateTime.now());
        word.setUpdatedAt(LocalDateTime.now());
        return wordRepository.save(word);
    }
    
    public Word updateWord(Long id, Word wordDetails) {
        Optional<Word> wordOptional = wordRepository.findById(id);
        if (wordOptional.isPresent()) {
            Word word = wordOptional.get();
            word.setEnglish(wordDetails.getEnglish());
            word.setChinese(wordDetails.getChinese());
            word.setGrade(wordDetails.getGrade());
            word.setUnit(wordDetails.getUnit());
            word.setUpdatedAt(LocalDateTime.now());
            return wordRepository.save(word);
        }
        return null;
    }
    
    public Word addWord(Word word) {
        word.setCreatedAt(LocalDateTime.now());
        word.setUpdatedAt(LocalDateTime.now());
        return wordRepository.save(word);
    }
    
    public boolean deleteWord(Long id) {
        if (wordRepository.existsById(id)) {
            wordRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Word> searchWords(String keyword) {
        List<Word> englishMatches = wordRepository.findByEnglishContainingIgnoreCase(keyword);
        List<Word> chineseMatches = wordRepository.findByChineseContainingIgnoreCase(keyword);
        
        // 合并结果并去重
        chineseMatches.addAll(englishMatches);
        return chineseMatches.stream().distinct().collect(java.util.stream.Collectors.toList());
    }
    
    public List<Word> getRandomWords(int count) {
        List<Word> allWords = wordRepository.findAll();
        if (allWords.size() <= count) {
            return allWords;
        }
        
        // 随机选择count个单词
        Collections.shuffle(allWords);
        return allWords.stream()
                .limit(count)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // 按年级获取单词
    public List<Word> getWordsByGrade(Integer grade) {
        return wordRepository.findByGrade(grade);
    }
    
    // 按年级和单元获取单词
    public List<Word> getWordsByGradeAndUnit(Integer grade, Integer unit) {
        return wordRepository.findByGradeAndUnit(grade, unit);
    }
    
    // 按年级随机获取单词
    public List<Word> getRandomWordsByGrade(Integer grade, int count) {
        List<Word> words = wordRepository.findByGrade(grade);
        if (words.size() <= count) {
            return words;
        }
        
        // 随机选择count个单词
        Collections.shuffle(words);
        return words.stream()
                .limit(count)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // 按年级和单元随机获取单词
    public List<Word> getRandomWordsByGradeAndUnit(Integer grade, Integer unit, int count) {
        List<Word> words = wordRepository.findByGradeAndUnit(grade, unit);
        if (words.size() <= count) {
            return words;
        }
        
        // 随机选择count个单词
        Collections.shuffle(words);
        return words.stream()
                .limit(count)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 根据用户的错题历史生成加权的考试单词列表
     * 优先考察错题，错题次数越多，出现概率越高
     * 
     * @param userId 用户ID
     * @param grade 年级
     * @param unit 单元（可选，为0表示全部单元）
     * @param count 考试题目数
     * @return 加权后的单词列表
     */
    public List<Word> getWeightedRandomWordsByGrade(Long userId, Integer grade, Integer unit, int count) {
        logger.info("=== 开始生成加权考试题目 ===");
        logger.info("userId: {}, grade: {}, unit: {}, count: {}", userId, grade, unit, count);
        
        // 1. 获取该年级（或年级+单元）的所有单词
        List<Word> allWords;
        if (unit == null || unit == 0) {
            allWords = wordRepository.findByGrade(grade);
            logger.info("按年级查询单词数: {}", allWords.size());
        } else {
            allWords = wordRepository.findByGradeAndUnit(grade, unit);
            logger.info("按年级和单元查询单词数: {}", allWords.size());
        }
        
        if (allWords.isEmpty()) {
            logger.warn("未找到该年级的单词");
            return new ArrayList<>();
        }
        
        // 2. 获取该用户的错题记录，按错误次数降序排列
        java.util.List<com.example.englishwords.entity.UserWordError> userErrors = 
            userWordErrorRepository.findByUserIdOrderByErrorCountDesc(userId);
        logger.info("用户错题记录数: {}", userErrors.size());
        
        // 3. 创建错词ID到错误次数的映射
        java.util.Map<Long, Integer> errorCountMap = new java.util.HashMap<>();
        for (com.example.englishwords.entity.UserWordError error : userErrors) {
            errorCountMap.put(error.getWordId(), error.getErrorCount());
        }
        
        // 4. 构建加权列表：错题重复出现，正确题出现一次
        List<Word> weightedWords = new ArrayList<>();
        
        // 先加入所有错题（按错误次数重复）
        for (Word word : allWords) {
            Integer errorCount = errorCountMap.get(word.getId());
            if (errorCount != null && errorCount > 0) {
                // 错题权重：基础1次 + 错误次数的一半
                // 例如：错1次出现2次，错2次出现3次，错3次出现4次
                int weight = 1 + (errorCount / 2);
                for (int i = 0; i < weight; i++) {
                    weightedWords.add(word);
                }
            } else {
                // 未出错的题只出现一次
                weightedWords.add(word);
            }
        }
        
        logger.info("加权后单词列表大小: {}", weightedWords.size());
        
        // 5. 打乱顺序后取前count个单词
        Collections.shuffle(weightedWords);
        
        List<Word> result;
        if (weightedWords.size() <= count) {
            result = weightedWords;
        } else {
            result = weightedWords.stream()
                    .limit(count)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        logger.info("最终返回单词数: {}", result.size());
        logger.info("=== 加权考试题目生成完成 ===");
        return result;
    }
}