package com.example.englishwords.repository;

import com.example.englishwords.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByEnglishContainingIgnoreCase(String english);
    List<Word> findByChineseContainingIgnoreCase(String chinese);
    boolean existsByEnglish(String english);
    
    // 按年级查询
    List<Word> findByGrade(Integer grade);
    
    // 按年级和单元查询
    List<Word> findByGradeAndUnit(Integer grade, Integer unit);
}