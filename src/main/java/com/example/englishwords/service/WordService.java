package com.example.englishwords.service;

import com.example.englishwords.entity.Word;
import com.example.englishwords.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    
    @Autowired
    private WordRepository wordRepository;
    
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
}