package com.example.englishwords.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 单词反馈记录实体类
 * 用于记录学生对单词错误的反馈
 */
@Data
@Entity
@Table(name = "word_feedback")
public class WordFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的单词ID
     */
    @Column(name = "word_id", nullable = false)
    private Long wordId;
    
    /**
     * 反馈的用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 反馈的用户名
     */
    @Column(name = "username", length = 100)
    private String username;
    
    /**
     * 单词录入的老师ID（用于关联反馈给具体的老师）
     */
    @Column(name = "teacher_id")
    private Long teacherId;
    
    /**
     * 原单词英文
     */
    @Column(name = "original_english", length = 200)
    private String originalEnglish;
    
    /**
     * 原单词中文
     */
    @Column(name = "original_chinese", length = 200)
    private String originalChinese;
    
    /**
     * 反馈内容描述
     */
    @Column(name = "feedback_content", columnDefinition = "TEXT")
    private String feedbackContent;
    
    /**
     * 反馈状态：PENDING-待处理, RESOLVED-已解决, REJECTED-已拒绝
     */
    @Column(name = "status", length = 20)
    private String status = "PENDING";
    
    /**
     * 管理员处理备注
     */
    @Column(name = "admin_remark", columnDefinition = "TEXT")
    private String adminRemark;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 处理时间
     */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    /**
     * 老师名字（录入单词的老师）
     */
    @Column(name = "teacher_name", length = 100)
    private String teacherName;
    
    /**
     * 单词的年级
     */
    @Column(name = "grade")
    private Integer grade;
    
    /**
     * 单词的单元
     */
    @Column(name = "unit")
    private Integer unit;
}
