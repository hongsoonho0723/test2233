package com.multicampus.b01.config;


import com.multicampus.b01.security.CustomUserDetailsService;
import com.multicampus.b01.security.handler.Custom403Handler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)


public class CustomSecurityConfig {
    //Injection 필요 (remember-me 설정에 필요한 쿠키 정보를 데이터베이스 p
    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("------------configure-------------------");

        http.formLogin().loginPage("/member/login"); //로그인화면에서 로그인을 진행한다는 설정

        http.csrf().disable();

        http.rememberMe().key("12345678")   //쿠키를 생성할때 필요한 인코딩 키값
                .tokenRepository(persistenTokenRepository())    //정보를 저장하는 공간
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(60*60*24*30);

        return http.build();
    }
    @Bean
    public PersistentTokenRepository persistenTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        log.info("------------web configure-------------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }



}
