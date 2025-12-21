package com.example.englishwords.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 基于SM4算法的密码编码器
 */
@Component
public class SM4PasswordEncoder implements PasswordEncoder {
    
    @Override
    public String encode(CharSequence rawPassword) {
        // 使用SM4加密算法加密密码
        return SM4Util.encrypt(rawPassword.toString());
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 验证密码是否匹配
        return SM4Util.matches(rawPassword.toString(), encodedPassword);
    }
}