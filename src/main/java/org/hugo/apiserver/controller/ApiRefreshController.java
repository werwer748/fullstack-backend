package org.hugo.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.util.CustomJwtException;
import org.hugo.apiserver.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ApiRefreshController {
    // 필터로 만들 수도 있지만 컨트롤러로도 처리가 가능하다~
    // 예제상 편하게 처리하기위해 RequestMapping
    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            String refreshToken
    ) {
        if (refreshToken == null) {
            throw new CustomJwtException("NULL_REFRESH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJwtException("INVALID STRING");
        }

        String accessToken = authHeader.substring(7);
        // AccessToken이 만료되지 않은 경우
        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Refresh 토큰 검증
        Map<String, Object> claims = JwtUtil.validateToken(refreshToken);
        log.info("refresh ... claims: " + claims);

        String newAccessToken = JwtUtil.generateToken(claims, 10);

        String newRefreshToken = checkTime((Integer) claims.get("exp")) ?
                JwtUtil.generateToken(claims, 60 * 24)
                :
                refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // refreshToken 만료시간이 얼마 남지 않은경우 함꼐 재발행하기위한 메서드
    private boolean checkTime(Integer exp) {
        // JWT exp를 날짜로 변환
        Date expDate = new Date((long) exp * (1000));

        // 현재 시간과의 차이 계산 - 밀리세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();
        // 분단위 계산
        long leftMin = gap / (1000 * 60);

        // 한시간보다 적게 남았는지 확인
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try {
            JwtUtil.validateToken(token);
        } catch (CustomJwtException ex) {
            if (ex.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }
}
