package org.hugo.apiserver.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JwtUtil {

    private static String key = "1234567890123456789012345678901234567890"; // 키값이 짧으면 제대로 작동안됨 (30자 이상 쓰면 됨)

    // 토큰 생성
    public static String generateToken(Map<String, Object> valueMap, int min) {
        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(JwtUtil.key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();
    }

    // 토큰 검증
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JwtUtil.key.getBytes(StandardCharsets.UTF_8));

            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증? - 실패시 에러라고 함
                    .getBody();

        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJwtException("MalFormed");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJwtException("Expired");
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJwtException("Invalid");
        } catch (JwtException jwtException) {
            throw new CustomJwtException("JWTError");
        } catch (Exception e) {
            throw new CustomJwtException("Error");
        }
        return claim;
    }
}
