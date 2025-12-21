package com.example.englishwords.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "word_submission")
public class WordSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String english;
    
    @Column(nullable = false)
    private String chinese;
    
    @Column(nullable = false)
    private Integer grade;
    
    @Column(nullable = false)
    private Integer unit;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "teacher_name")
    private String teacherName;
    
    @Column(name = "status", columnDefinition = "varchar(20) default 'PENDING'")
    private String status = "PENDING"; // PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝
    
    @Column(name = "admin_remark", columnDefinition = "TEXT")
    private String adminRemark; // 管理员审批意见
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
