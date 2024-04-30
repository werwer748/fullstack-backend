package org.hugo.apiserver.config;

import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.controller.formatter.LocalDateFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Log4j2
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {

        log.info("-------------------------------------");
        log.info("addFormatters 작동확인");

        registry.addFormatter(new LocalDateFormatter());
    }

    //? 해당 설정은 시큐리티 설정쪽으로 옮겨야 함.
//    @Override // 프로젝트 전역 CORS 설정
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 어떤 경로에 cors적용을 할 것인지
//                .maxAge(500) // 얼마나 오랫동안 preflight 요청이 캐싱될 수 있는지
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // 어떤 메서드들을 허용해 줄 것인지
//                .allowedOrigins("*"); // 어디에서부터 들어오는 연결을 허락할 것인가
//    }
}
