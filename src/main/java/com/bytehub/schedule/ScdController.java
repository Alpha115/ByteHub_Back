package com.bytehub.schedule;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/scd")
public class ScdController {

	Map<String, Object> resp = null;
	private final ScdService svc;
	
	// 일정(1일)일의 내용을 조회합니다.(일정정보, 해당날짜 일정 총 갯수)
	@GetMapping("/day/{date}")
	public Map<String, Object> day(@PathVariable Date date){
		resp=new HashMap<String, Object>();
		
		return resp;
	}
	
	// 일정을 입력합니다.
	@PostMapping("/insert")
	public Map<String, Object> insert(@RequestBody ScdDTO info){
		resp=new HashMap<String, Object>();
		resp.put("success", svc.insert(info));
		return resp;
	}
	
	// ▲일정(start~end)을 불러오는 요청
	
	
}
