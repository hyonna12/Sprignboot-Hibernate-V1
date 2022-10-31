package site.metacoding.white.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.white.config.auth.JwtAuthenticationFilter;
import site.metacoding.white.config.auth.JwtAuthorizationFilter;
import site.metacoding.white.domain.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Configuration // ioc 등록
public class FilterConfig {

  private final UserRepository userRepository; // DI (스프링 IoC 컨테이너에서 옴)

  @Bean // 필터를 ioc에 등록(서버 실행시)
  public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
    log.debug("디버그 : 인증 필터 등록");
    FilterRegistrationBean<JwtAuthenticationFilter> bean = new FilterRegistrationBean<>(
        new JwtAuthenticationFilter(userRepository)); // 필터등록
    bean.addUrlPatterns("/login"); // 언제 필터 실행될지
    bean.setOrder(1); // 낮은 순서대로 실행
    return bean;
  }

  @Bean // 필터를 ioc에 등록(서버 실행시)
  public FilterRegistrationBean<JwtAuthorizationFilter> JwtAuthorizationFilterRegister() {
    log.debug("디버그 : 인가 필터 등록");
    FilterRegistrationBean<JwtAuthorizationFilter> bean = new FilterRegistrationBean<>(
        new JwtAuthorizationFilter()); // 필터등록
    bean.addUrlPatterns("/s/*"); // 언제 필터 실행될지 / 원래 두갠인데, 이 친구만 예외
    bean.setOrder(2);
    return bean;
  }
  // 실행되면 ioc에 filterconfig, filterregistrationbean 등록
  // /login 되면 jwt 필터 등록
}
