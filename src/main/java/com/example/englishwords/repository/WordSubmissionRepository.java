package com.example.englishwords.repository;

import com.example.englishwords.entity.WordSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordSubmissionRepository extends JpaRepository<WordSubmission, Long> {
    
    // 根据状态查询
    List<WordSubmission> findByStatusOrderBySubmittedAtDesc(String status);
    
    // 根据老师ID查询
    List<WordSubmission> findByTeacherIdOrderBySubmittedAtDesc(Long teacherId);
    
    // 查询所有待审批的提交
    List<WordSubmission> findByStatusOrderBySubmittedAtAsc(String status);
    
    // 统计待审批数量
    long countByStatus(String status);
}
