package com.example.englishwords.repository;

import com.example.englishwords.entity.UserWordError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWordErrorRepository extends JpaRepository<UserWordError, Long> {
    List<UserWordError> findByUserId(Long userId);
    Optional<UserWordError> findByUserIdAndWordId(Long userId, Long wordId);
    List<UserWordError> findByUserIdOrderByErrorCountDesc(Long userId);
}