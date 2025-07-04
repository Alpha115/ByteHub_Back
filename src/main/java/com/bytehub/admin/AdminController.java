package com.bytehub.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService service;
	Map<String, Object> resp=null;
	
	// 관리자의 아이디로 직원을 퇴사처리하는 함수입니다.
	@GetMapping("/withdraw/{id}")
	public Map<String, Object> withdraw(@PathVariable String id){
		resp=new HashMap<String, Object>();
		resp.put("success", service.withdraw(id));
		return resp;
	}
	
	// 관리자의 아이디로 직원의 리스트(member_list)를 불러오는 함수입니다.
	@GetMapping("/memberList")
	public Map<String, Object> list(){
		resp=new HashMap<String, Object>();
		resp.put("member_list", service.list());
		return resp;
	}
	
}
