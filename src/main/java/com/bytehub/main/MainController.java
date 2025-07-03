package com.bytehub.main;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import com.bytehub.member.MemberService;

@Slf4j
@CrossOrigin
@RestController
public class MainController {

	// ▼ json 형식의 response
	Map<String, Object> resp = null;

	@Autowired MemberService service;

	@GetMapping("/")
	public Map<String, Object> home() {
		resp=new HashMap<String, Object>();
		resp.put("msg", "메인 페이지");
		return resp;
	}
	// 확인


}
