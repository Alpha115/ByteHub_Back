package com.bytehub.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class MainController {

	// ▼ json 형식의 response
	Map<String, Object> resp = null;

	@GetMapping("/")
	public Map<String, Object> home() {
		resp=new HashMap<String, Object>();
		resp.put("msg", "메인 페이지");
		return resp;
	}

}
