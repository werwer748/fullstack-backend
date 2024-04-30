package org.hugo.apiserver.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.MemberDto;
import org.hugo.apiserver.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class ApiLoginSuccessHandler implements AuthenticationSuccessHandler { // 인증 성고시 처리를 어떻게 할건지 작성

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("------------ onAuthenticationSuccess ------------");
        log.info(authentication);
        log.info("------------------------");

        MemberDto memberDto = (MemberDto) authentication.getPrincipal(); // 인증된 정보를 authentication에서 꺼내온다.

        // dto에서 claims를 Map으로 만들어 둠
        Map<String, Object> claims = memberDto.getClaims();

        String accessToken = JwtUtil.generateToken(claims, 10);
        String refreshToken = JwtUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        Gson gson = new Gson();

        // claims를 json으로
        String jsonStr = gson.toJson(claims);

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
