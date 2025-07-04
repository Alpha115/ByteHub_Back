package com.bytehub.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bytehub.utils.LoginChecker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//configure file

@CrossOrigin
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
	
	private final LoginChecker checker;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		
		ArrayList<String> excludeURL=new ArrayList<String>();
		// ★여기에다가 어떤 url에서 로그인 체크 제외할건지 설정하시면 됩니다~
		excludeURL.add("/**");
		
		ArrayList<String> addCheckURL=new ArrayList<String>();
		// ★여기에다가 어떤 url을 체크할건지 설정하시면 됩니다
		// USAGE: addCheckURL.add("/example");
		
		
		registry.addInterceptor(checker).excludePathPatterns(excludeURL).addPathPatterns(addCheckURL);
		
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.httpBasic().disable().csrf().disable().build();
	}

	

}
