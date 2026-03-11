package com.admin.backendadmin.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类：生成、解析、验证Token
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:abcdefghijklmnopqrstuvwxyz1234567890abcdef}")
    private String secret;

    @Value("${jwt.expire:7200000}") // 默认2小时
    private Long expire;

    /**
     * 生成Token
     * @param claims 自定义载荷（如userId、role）
     * @return Token字符串
     */
    public String generateToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key)
                .compact();
    }

    /**
     * 解析Token获取载荷
     * @param token Token字符串
     * @return 载荷Claims
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否过期
     */
    public boolean isExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }

    /**
     * 从Token中获取userId
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取role
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }
}
