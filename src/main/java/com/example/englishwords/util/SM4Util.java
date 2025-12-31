package com.example.englishwords.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

/**
 * SM4加密工具类
 */
@Component
public class SM4Util {
    
    static {
        // 添加BouncyCastleProvider支持
        Security.addProvider(new BouncyCastleProvider());
    }
    
    // 默认密钥（16字节/128位）- SM4算法要求
    // "1234567890123456" = 16字节
    private static final String DEFAULT_KEY = "1234567890123456";
    
    /**
     * SM4加密
     * @param content 待加密内容
     * @param key 密钥
     * @return 加密后的内容
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "SM4");
            Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("SM4加密失败", e);
        }
    }
    
    /**
     * SM4解密
     * @param content 待解密内容
     * @param key 密钥
     * @return 解密后的内容
     */
    public static String decrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "SM4");
            Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(content));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("SM4解密失败", e);
        }
    }
    
    /**
     * 使用默认密钥加密
     * @param content 待加密内容
     * @return 加密后的内容
     */
    public static String encrypt(String content) {
        return encrypt(content, DEFAULT_KEY);
    }
    
    /**
     * 使用默认密钥解密
     * @param content 待解密内容
     * @return 解密后的内容
     */
    public static String decrypt(String content) {
        return decrypt(content, DEFAULT_KEY);
    }
    
    /**
     * 验证密码是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            String decrypted = decrypt(encodedPassword);
            return decrypted.equals(rawPassword);
        } catch (Exception e) {
            return false;
        }
    }
}