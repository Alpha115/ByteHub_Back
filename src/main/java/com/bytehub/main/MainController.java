package com.bytehub.main;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bytehub.member.MemberService;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class MainController {

	// ▼ json 형식의 response
	Map<String, Object> resp = null;

	private final MemberService service;

	@GetMapping("/")
	public Map<String, Object> home() {
		resp=new HashMap<String, Object>();
		resp.put("msg", "메인 페이지");
		return resp;
	}
	// 확인


}
