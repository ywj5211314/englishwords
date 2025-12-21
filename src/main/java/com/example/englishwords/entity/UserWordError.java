package com.example.englishwords.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_word_error")
public class UserWordError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "word_id", nullable = false)
    private Long wordId;
    
    @Column(name = "error_count", nullable = false)
    private Integer errorCount = 0;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}