package com.bytehub.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
	
	private final LoginChecker checker = new LoginChecker();
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		
		ArrayList<String> excludeURL=new ArrayList<String>();
		// ★여기에다가 어떤 url에서 로그인 체크 제외할건지 설정하시면 됩니다~
		excludeURL.add("/");
		
		ArrayList<String> addCheckURL=new ArrayList<String>();
		// ★여기에다가 어떤 url을 체크할건지 설정하시면 됩니다
		// USAGE: addCheckURL.add("/example");
		addCheckURL.add("/*");
		
		registry.addInterceptor(checker).excludePathPatterns(excludeURL).addPathPatterns(addCheckURL);
		
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000", "http://localhost:3001", "http://localhost:80")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true)
			.maxAge(3600);
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
