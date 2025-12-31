package com.example.englishwords.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "word")
public class Word {
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
    
    /**
     * 单词录入的老师ID（有些单词是管理员直接漂加，查询时为null）
     */
    @Column(name = "teacher_id")
    private Long teacherId;
    
    /**
     * 单词录入的老师名字
     */
    @Column(name = "teacher_name")
    private String teacherName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}