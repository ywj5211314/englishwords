package com.example.englishwords.service;

import com.example.englishwords.entity.Word;
import com.example.englishwords.entity.WordSubmission;
import com.example.englishwords.repository.WordRepository;
import com.example.englishwords.repository.WordSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WordSubmissionService {
    
    @Autowired
    private WordSubmissionRepository submissionRepository;
    
    @Autowired
    private WordRepository wordRepository;
    
    /**
     * 验证提交的单词是否已存在（根据英文单词回源）
     */
    public java.util.List<String> validateWordSubmission(WordSubmission submission) {
        java.util.List<String> duplicateWords = new java.util.ArrayList<>();
        
        // 检查单词是否存在于word表中
        if (wordRepository.existsByEnglish(submission.getEnglish())) {
            duplicateWords.add(submission.getEnglish());
        }
        
        return duplicateWords;
    }
    
    /**
     * 老师提交单词
     */
    public WordSubmission submitWord(WordSubmission submission) {
        submission.setStatus("PENDING");
        submission.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }
    
    /**
     * 获取所有待审批的单词
     */
    public List<WordSubmission> getPendingSubmissions() {
        return submissionRepository.findByStatusOrderBySubmittedAtAsc("PENDING");
    }
    
    /**
     * 根据老师ID获取提交记录
     */
    public List<WordSubmission> getSubmissionsByTeacherId(Long teacherId) {
        return submissionRepository.findByTeacherIdOrderBySubmittedAtDesc(teacherId);
    }
    
    /**
     * 管理员批准单词
     */
    @Transactional
    public WordSubmission approveSubmission(Long id, String adminRemark) {
        Optional<WordSubmission> optionalSubmission = submissionRepository.findById(id);
        if (optionalSubmission.isPresent()) {
            WordSubmission submission = optionalSubmission.get();
            submission.setStatus("APPROVED");
            submission.setAdminRemark(adminRemark);
            submission.setReviewedAt(LocalDateTime.now());
            
            // 将单词添加到正式单词表
            Word word = new Word();
            word.setEnglish(submission.getEnglish());
            word.setChinese(submission.getChinese());
            word.setGrade(submission.getGrade());
            word.setUnit(submission.getUnit());
            word.setTeacherId(submission.getTeacherId());
            word.setTeacherName(submission.getTeacherName());
            word.setCreatedAt(LocalDateTime.now());
            word.setUpdatedAt(LocalDateTime.now());
            wordRepository.save(word);
            
            return submissionRepository.save(submission);
        }
        return null;
    }
    
    /**
     * 管理员拒绝单词
     */
    public WordSubmission rejectSubmission(Long id, String adminRemark) {
        Optional<WordSubmission> optionalSubmission = submissionRepository.findById(id);
        if (optionalSubmission.isPresent()) {
            WordSubmission submission = optionalSubmission.get();
            submission.setStatus("REJECTED");
            submission.setAdminRemark(adminRemark);
            submission.setReviewedAt(LocalDateTime.now());
            return submissionRepository.save(submission);
        }
        return null;
    }
    
    /**
     * 批量批准单词
     */
    @Transactional
    public java.util.Map<String, Object> batchApproveSubmissions(java.util.List<Long> ids, String adminRemark) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        for (Long id : ids) {
            try {
                WordSubmission submission = approveSubmission(id, adminRemark);
                if (submission != null) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }
        
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return result;
    }
    
    /**
     * 批量拒绝单词
     */
    @Transactional
    public java.util.Map<String, Object> batchRejectSubmissions(java.util.List<Long> ids, String adminRemark) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        for (Long id : ids) {
            try {
                WordSubmission submission = rejectSubmission(id, adminRemark);
                if (submission != null) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }
        
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return result;
    }
    
    /**
     * 统计待审批数量
     */
    public long countPendingSubmissions() {
        return submissionRepository.countByStatus("PENDING");
    }
    
    /**
     * 获取单词提交详情
     */
    public Optional<WordSubmission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }
}
