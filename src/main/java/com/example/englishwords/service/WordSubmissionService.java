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
