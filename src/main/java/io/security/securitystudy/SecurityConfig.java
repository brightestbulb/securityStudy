package io.security.securitystudy;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity  // Security 관련 여러 클래스들을 설정(= 웹보안 활성화)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http  // 인가
                .authorizeRequests()
                .anyRequest().authenticated();  // 어떤 요청에도 인증을 받는다(= 인가정책)
        http  // 인증
                .formLogin();
    }
}
