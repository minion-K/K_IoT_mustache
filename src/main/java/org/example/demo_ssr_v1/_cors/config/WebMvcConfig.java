package org.example.demo_ssr_v1._cors.config;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._cors.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정 클래스
 */
// @Component 클래스 내부에서 @Bean 어노테이션을 사용해야 한다면 @Configuration 사용
@Configuration // 클래스 내부에도 IoC 대상 여부 확인
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;

    // 인터셉터는 당연히 여러 개 등록 가능
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 설정에 LoginInterceptor를 등록
        // 2. 인터셉처가 동작할 URL 패턴 지정
        // 3. 어떤 URL 요청이 로그인 여부를 필요할 지 확인
        //  /board/** >> 해당 엔드포인트 모두 검사
        //  /user/**  >> 해당 엔드포인트 모두 검사
        //  -> 단, 특정 URL은 제외
        registry.addInterceptor(loginInterceptor)
                // /** -> 모든 URL이 제외 대상이 됨
                .addPathPatterns("/board/**", "/user/**", "/reply/**")
                .excludePathPatterns(
                        "/login",
                        "/join",
                        "/logout",
                        "/board/list",
                        "/",
                        "/board/{id:\\d+}",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.io",
                        "/h2-condole/**",
                        "/board/search"
                );
                // \\d+: 정규 표현식으로 1개 이상의 숫자를 의미
                // /board/1, /board/1234 허용
                // /board/abc 같은 경우 매칭되지 않음
    }
}
