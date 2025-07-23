package com.bytehub.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginChecker implements HandlerInterceptor {
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean pass = true;
		log.info("InterCeptorURI: {}", request.getRequestURI());
		HttpSession session = request.getSession();
		String loginId = (String) session.getAttribute("loginId");

		if (loginId == null || loginId.equals("")) {	// 로긴이 안돼있다면 퉤 
			pass = false;
			String context=request.getContextPath();	// context 경로 추출
			response.sendRedirect("http://localhost:3000/");	// Context 경로도 같이줘야함, context경로가 없으면 걍 /가 들어갈 것이다
			// 근데 어카냐...
		}
		return pass;
	}

}
