package com.example.englishwords.service;

import com.example.englishwords.entity.*;
import com.example.englishwords.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {
    
    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private ExamRecordRepository examRecordRepository;
    
    @Autowired
    private UserWordErrorRepository userWordErrorRepository;
    
    @Autowired
    private UserService userService;
    
    public List<Word> generateExamPaper(int count) {
        List<Word> allWords = wordRepository.findAll();
        if (allWords.size() <= count) {
            return allWords;
        }
        
        // 随机选择count个单词
        Collections.shuffle(allWords);
        return allWords.subList(0, count);
    }
    
    public Map<String, Object> submitExam(Long userId, Map<Long, String> answers, List<Word> examWords) {
        int correctCount = 0;
        int totalCount = examWords.size();
        
        // 记录用户答错的单词
        List<Long> wrongWordIds = new ArrayList<>();
        
        // 检查答案
        for (Word word : examWords) {
            String userAnswer = answers.getOrDefault(word.getId(), "").trim().toLowerCase();
            String correctAnswer = word.getEnglish().trim().toLowerCase();
            
            if (userAnswer.equals(correctAnswer)) {
                correctCount++;
            } else {
                wrongWordIds.add(word.getId());
            }
        }
        
        int wrongCount = totalCount - correctCount;
        int score = (int) Math.round(((double) correctCount / totalCount) * 100);
        
        // 保存考试记录
        ExamRecord examRecord = new ExamRecord();
        examRecord.setUserId(userId);
        examRecord.setScore(score);
        examRecord.setTotalQuestions(totalCount);
        examRecord.setCorrectAnswers(correctCount);
        examRecord.setWrongAnswers(wrongCount);
        examRecord.setExamTimeSeconds(0); // 简化处理，实际应该记录考试用时
        examRecord.setCreatedAt(LocalDateTime.now());
        examRecordRepository.save(examRecord);
        
        // 更新用户错题记录
        updateErrorCounts(userId, wrongWordIds);
        
        // 如果考试通过(分数>=60)，给予积分奖励
        if (score >= 60) {
            // 基础积分+额外奖励积分
            int rewardPoints = 10 + (score - 60) / 10; // 每超过60分10分，奖励1积分
            userService.updateUserScore(userId, rewardPoints);
        }
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("correctCount", correctCount);
        result.put("wrongCount", wrongCount);
        result.put("totalCount", totalCount);
        result.put("passed", score >= 60);
        
        return result;
    }
    
    private void updateErrorCounts(Long userId, List<Long> wrongWordIds) {
        for (Long wordId : wrongWordIds) {
            Optional<UserWordError> userWordErrorOpt = userWordErrorRepository.findByUserIdAndWordId(userId, wordId);
            
            if (userWordErrorOpt.isPresent()) {
                // 更新错误次数
                UserWordError userWordError = userWordErrorOpt.get();
                userWordError.setErrorCount(userWordError.getErrorCount() + 1);
                userWordError.setUpdatedAt(LocalDateTime.now());
                userWordErrorRepository.save(userWordError);
            } else {
                // 创建新的错误记录
                UserWordError userWordError = new UserWordError();
                userWordError.setUserId(userId);
                userWordError.setWordId(wordId);
                userWordError.setErrorCount(1);
                userWordError.setUpdatedAt(LocalDateTime.now());
                userWordErrorRepository.save(userWordError);
            }
        }
    }
    
    public List<ExamRecord> getUserExamRecords(Long userId) {
        return examRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<UserWordError> getUserWordErrors(Long userId) {
        return userWordErrorRepository.findByUserIdOrderByErrorCountDesc(userId);
    }
}