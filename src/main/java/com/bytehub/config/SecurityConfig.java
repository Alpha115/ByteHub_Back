package com.bytehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


//configure file
@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable() // csrf보호를 끄고 api 서버에서는 보통 csrf 를 비활성화함함
			.authorizeRequests()
				.antMatchers("/member/join", "/member/overlay/**").permitAll()
				.anyRequest().authenticated() // 위 url 외 의모든 요청은 인증 필요요
			.and()
			.formLogin().disable() // 기본 로그인 폼 비활성화
			.httpBasic().disable(); // HTTP Basic 인증을 비활성화
		return http.build();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
