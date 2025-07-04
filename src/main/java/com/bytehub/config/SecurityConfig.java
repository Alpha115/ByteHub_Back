package com.bytehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bytehub.utils.LoginChecker;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//configure file

@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
	
	private final LoginChecker checker;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// ★여기에다가 addPathPatterns 추가해서 어떤 url에서 로그인 체크할건지 설정하시면 됩니다~
		registry.addInterceptor(checker).excludePathPatterns("/login");
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.httpBasic().disable().build();
		// ^^^ csrf 문제가 생기면 csrf도 disable합니다.
		// 그런데 next.js는 안뜬대서…
	}

	

}
