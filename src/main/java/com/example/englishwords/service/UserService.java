package com.example.englishwords.service;

import com.example.englishwords.entity.User;
import com.example.englishwords.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User register(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public Optional<User> login(String username, String password) {
        System.out.println("Attempting to find user: " + username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        System.out.println("User found in DB: " + userOptional.isPresent());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("Stored password hash: " + user.getPassword());
            System.out.println("Provided password: " + password);
            
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("Password matches: " + passwordMatches);
            
            if (passwordMatches) {
                return userOptional;
            }
        }
        return Optional.empty();
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public User updateUserScore(Long userId, int score) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setTotalScore(user.getTotalScore() + score);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        return null;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}