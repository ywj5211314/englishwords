package com.example.englishwords.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exam_record")
public class ExamRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "score", nullable = false)
    private Integer score;
    
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;
    
    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;
    
    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers;
    
    @Column(name = "exam_time_seconds", nullable = false)
    private Integer examTimeSeconds;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}