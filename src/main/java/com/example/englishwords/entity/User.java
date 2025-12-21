package com.example.englishwords.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "nick_name")
    private String nickName;
    
    @Column(name = "total_score", columnDefinition = "int default 0")
    private Integer totalScore = 0;
    
    @Column(name = "role", columnDefinition = "varchar(50) default 'USER'")
    private String role = "USER";
    
    @Column(name = "grade")
    private Integer grade;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}