package org.hugo.apiserver.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.MemberDto;
import org.hugo.apiserver.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * jwt 문자열을 체크해야하는데 시큐리티(스프링웹시큐리티)는 필터를 통해 처리한다.
 * 정상적인 토큰일때 통과시키는 기능을 말함
 * OncePerRequestFilter: 모든 요청에 거는 필터
 * 외에 굉장히 많은 필터를 제공한다.
 */
@Log4j2
public class JwtCheckFilter extends OncePerRequestFilter {
    @Override // 검사를 안해도 되는 경우를 지정하기위해 사용
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Preflight요청은 체크하지 않음
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURI();
        log.info("check uri = " + path);
        // 체크안하는거 return true
        if (path.startsWith("/api/member")) {
            return true;
        };

        //이미지 조회 경로는 체크하지 않음
        if(path.startsWith("/api/products/view/")) {
            return true;
        }


        // 체크하는거 return false
        return false;
    }

    @Override
    protected void doFilterInternal( // 필터 통과시 다음 동작을 지정한다.
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("---------------- doFilterInternal ----------------");
        log.info("---------------- doFilterInternal ----------------");
        log.info("---------------- doFilterInternal ----------------");

        String authHeaderStr = request.getHeader("Authorization");

        try {
            // Bearer // 공백포함 7글자 뒤로 토큰
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JwtUtil.validateToken(accessToken); // 토큰 내용 검증

            log.info("Jwt claims" + claims);

            // destination
//            filterChain.doFilter(request, response); // 통과 - 권한 체크 전

            // 권한 체크로직 추가
            String email = (String) claims.get("email");
            log.info(email);
            String pw = (String) claims.get("pw");
            log.info(pw);
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDto memberDto = new MemberDto(email, pw, nickname, social, roleNames);

            log.info("----------- claim role check ------------");
            log.info(memberDto);
            log.info(memberDto.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken //SecurityContext가 사용하는 토큰을 만드는것.
                    = new UsernamePasswordAuthenticationToken(memberDto, pw, memberDto.getAuthorities()); // pw아니고 token을 넣는 방식을 고려해보자..담번엔

            //! 이 방법의 단점은 토큰이 호출 될 때 마다 시큐리티컨텍스트에 집어넣고 그걸 이용해서 PreAuthorize를 실행해야한다는 것.(무상태기때문에)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT CHeck Error...........");
            log.error(e);
            log.error(e.getMessage());

            Gson gson = new Gson();
            String message = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.print(message);
            printWriter.close();
        }


    }
}
