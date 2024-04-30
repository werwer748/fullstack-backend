package org.hugo.apiserver.config;

//? 여러가지 import할 때 TIP: webflux 같은 거 쓸 때 아니면 reactive 붙은거 안써도 됨.(뭔지도 모름)
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.security.filter.JwtCheckFilter;
import org.hugo.apiserver.security.handler.ApiLoginFailHandler;
import org.hugo.apiserver.security.handler.ApiLoginSuccessHandler;
import org.hugo.apiserver.security.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity // 어노테이션으로 컨트롤러에서 권한 체크하기위해 추가
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("---------------------security config------------------------");

        httpSecurity.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        // api서버는 기본적으로 stateless 하게 유지 되야함. 그래서 세션은 사용하지 않도록 설정을 해야한다.
        httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        // csrf => request 위조 방지: 사용하려면 자질구레한 일들이 많아짐. 일반적으로 api서버에서는 안쓸려고 많이 한다고 함.
//        httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // 로그인할 경로를 설정 => 원래는 api서버기 때문에 화면이 없는데 로그인용 화면만 잠깐 제공하도록 설정
        httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> {
            httpSecurityFormLoginConfigurer.loginPage("/api/member/login"); // 여기까지하고 경로 접속시 404 에러페이지.
            httpSecurityFormLoginConfigurer.successHandler(new ApiLoginSuccessHandler()); // 로그인 성공시 어떻게 처리할것인지
            httpSecurityFormLoginConfigurer.failureHandler(new ApiLoginFailHandler()); // 로그인 실패시 어떻게 처리 할 것인지.
        });

        // UsernamePasswordAuthenticationFilter.class 필터 작동전에 먼저 JwtCheckFilter 적용해줘~
        httpSecurity.addFilterBefore(new JwtCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

        return httpSecurity.build();
    }

    @Bean // 필수로 필요한 패스워드 인코더 설정 - 사용자 비밀번호 암호화에 사용
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean //org.springframework.web.cors.CorsConfigurationSource
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); // header 메시지 관련
        configuration.setAllowCredentials(true); // 자격증명을 함께 요청하도록

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
