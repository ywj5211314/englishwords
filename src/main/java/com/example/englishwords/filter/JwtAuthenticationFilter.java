package com.example.englishwords.filter;

import com.example.englishwords.entity.User;
import com.example.englishwords.repository.UserRepository;
import com.example.englishwords.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 跳过不需要认证的公共接口
        String requestURI = request.getRequestURI();
        System.out.println("Request URI: " + requestURI);
        if (requestURI.equals("/api/user/register") || requestURI.equals("/api/user/login") || requestURI.equals("/api/user/test-password") || requestURI.startsWith("/api/word-upload/")) {
            System.out.println("Skipping JWT filter for public endpoint: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        } else {
            System.out.println("Applying JWT filter for endpoint: " + requestURI);
        }
        
        String authorizationHeader = request.getHeader("Authorization");
        
        // 添加调试日志
        if (authorizationHeader != null) {
            System.out.println("Authorization header present: " + authorizationHeader);
        } else {
            System.out.println("No Authorization header");
        }
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            System.out.println("Token extracted: " + token);
            
            boolean isValid = jwtUtil.validateToken(token);
            boolean isExpired = false;
            try {
                isExpired = jwtUtil.isTokenExpired(token);
            } catch (Exception e) {
                System.out.println("Error checking token expiration: " + e.getMessage());
                isExpired = true; // Assume expired if we can't check
            }
            
            System.out.println("Token valid: " + isValid + ", Token expired: " + isExpired);
            
            if (isValid && !isExpired) {
                String username = jwtUtil.getUsernameFromToken(token);
                System.out.println("Username from token: " + username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 获取用户角色
                    User user = userRepository.findByUsername(username).orElse(null);
                    System.out.println("User found: " + (user != null));
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + (user != null ? user.getRole() : "USER"));
                    
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(username, null, java.util.Collections.singletonList(authority));
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } else {
                System.out.println("Token validation failed or token expired");
            }
        }
        
        filterChain.doFilter(request, response);
    }
}