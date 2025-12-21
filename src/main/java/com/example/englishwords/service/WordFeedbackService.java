package com.example.englishwords.service;

import com.example.englishwords.entity.WordFeedback;
import com.example.englishwords.repository.WordFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WordFeedbackService {
    
    @Autowired
    private WordFeedbackRepository feedbackRepository;
    
    /**
     * 创建反馈
     */
    public WordFeedback createFeedback(WordFeedback feedback) {
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setStatus("PENDING");
        return feedbackRepository.save(feedback);
    }
    
    /**
     * 获取所有反馈
     */
    public List<WordFeedback> getAllFeedbacks() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * 根据状态获取反馈
     */
    public List<WordFeedback> getFeedbacksByStatus(String status) {
        return feedbackRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    /**
     * 获取待处理的反馈
     */
    public List<WordFeedback> getPendingFeedbacks() {
        return feedbackRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }
    
    /**
     * 根据ID获取反馈
     */
    public Optional<WordFeedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }
    
    /**
     * 处理反馈 - 标记为已解决
     */
    public WordFeedback resolveFeedback(Long id, String adminRemark) {
        Optional<WordFeedback> optionalFeedback = feedbackRepository.findById(id);
        if (optionalFeedback.isPresent()) {
            WordFeedback feedback = optionalFeedback.get();
            feedback.setStatus("RESOLVED");
            feedback.setAdminRemark(adminRemark);
            feedback.setResolvedAt(LocalDateTime.now());
            return feedbackRepository.save(feedback);
        }
        return null;
    }
    
    /**
     * 拒绝反馈
     */
    public WordFeedback rejectFeedback(Long id, String adminRemark) {
        Optional<WordFeedback> optionalFeedback = feedbackRepository.findById(id);
        if (optionalFeedback.isPresent()) {
            WordFeedback feedback = optionalFeedback.get();
            feedback.setStatus("REJECTED");
            feedback.setAdminRemark(adminRemark);
            feedback.setResolvedAt(LocalDateTime.now());
            return feedbackRepository.save(feedback);
        }
        return null;
    }
    
    /**
     * 删除反馈
     */
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
    
    /**
     * 统计待处理反馈数量
     */
    public long countPendingFeedbacks() {
        return feedbackRepository.countByStatus("PENDING");
    }
}
