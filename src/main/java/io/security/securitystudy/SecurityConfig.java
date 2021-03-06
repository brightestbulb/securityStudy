package io.security.securitystudy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity  // Security 관련 여러 클래스들을 설정(= 웹보안 활성화)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http  // 인가
                .authorizeRequests()
                .anyRequest().authenticated();  // 어떤 요청에도 인증을 받는다(= 인가정책)

        http  // 인증
                .formLogin()
//                .loginPage("/loginPage")
                .defaultSuccessUrl("/")
                .failureUrl("/login")
                .usernameParameter("userId")
                .passwordParameter("passwd")
                .loginProcessingUrl("/login_proc")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override  //authentication : 인증에 성공했을때 받는 인증 객체
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        System.out.println("authentication : " + authentication.getName());  // 인증에 성공한 사용자 이름 출력
                        httpServletResponse.sendRedirect("/");  // root 페이지로 이동
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override  //AuthenticationException : 인증 예외 객체를 받음
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        System.out.println("exception : " + e.getMessage());
                        httpServletResponse.sendRedirect("/login");
                    }
                })
                // /loginPage 이 경로로 접근하는 사용자는 인증을 받지 않아도 이 페이지에 접근이 가능하도록 설정
                .permitAll();

        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate(); // 세션 무효화
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })
//                .deleteCookies("remember-me");  // "remember-me" 쿠키 삭제

        .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .tokenValiditySeconds(3600) // 3600 sec
                .userDetailsService(userDetailsService);

        http  // 동시 세션 제어
                .sessionManagement()
                .maximumSessions(2)  // 최대 허용 세션 갯수
                .maxSessionsPreventsLogin(true);  // 첫번째 사용자만 로그인 허용

        http  // 세션 고정 보호
                .sessionManagement()
                .sessionFixation().changeSessionId();
    }
}
