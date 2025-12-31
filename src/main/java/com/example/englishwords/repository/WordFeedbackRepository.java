package com.example.englishwords.repository;

import com.example.englishwords.entity.WordFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordFeedbackRepository extends JpaRepository<WordFeedback, Long> {
    
    /**
     * 根据状态查询反馈
     */
    List<WordFeedback> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * 查询所有反馈，按创建时间倒序
     */
    List<WordFeedback> findAllByOrderByCreatedAtDesc();
    
    /**
     * 根据用户ID查询反馈
     */
    List<WordFeedback> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据单词ID查询反馈
     */
    List<WordFeedback> findByWordId(Long wordId);
    
    /**
     * 统计待处理的反馈数量
     */
    long countByStatus(String status);
    
    /**
     * 根据老师ID查询反馈
     */
    List<WordFeedback> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    
    /**
     * 根据老师ID和状态查询反馈
     */
    List<WordFeedback> findByTeacherIdAndStatusOrderByCreatedAtDesc(Long teacherId, String status);
}
