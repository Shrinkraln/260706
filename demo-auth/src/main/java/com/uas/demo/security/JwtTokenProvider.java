package com.uas.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌工具 — 签发、解析、验证。
 * 与主项目共用 jjwt 0.12.5 版本。
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expireMs;

    public JwtTokenProvider(
            @Value("${demo.jwt.secret}") String secret,
            @Value("${demo.jwt.expire-hours}") long expireHours) {
        // jjwt 0.12+ 要求密钥 >= 256 bits；不足时右侧补 0
        byte[] keyBytes = new byte[32];
        byte[] src = secret.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, keyBytes, 0, Math.min(src.length, 32));
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expireMs = expireHours * 3600_000L;
    }

    /** 签发 token */
    public String generateToken(Long userId, String dlzh, Integer js) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("dlzh", dlzh)
                .claim("js", js)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireMs))
                .signWith(key)
                .compact();
    }

    /** 解析 token 中的 Claims（异常时返回 null） */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    /** 验证 token 是否有效 */
    public boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    /** 从 token 中提取 userId */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return Long.parseLong(claims.getSubject());
    }
}
