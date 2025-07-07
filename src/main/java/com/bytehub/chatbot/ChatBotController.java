package com.bytehub.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/keyword")
public class ChatBotController {
	
	private final ChatBotService service;	
	Map<String, Object> resp=null;
	
	@PostMapping("/insert")
	public Map<String, Object> insertKeyword(@RequestBody Map<String, Object> param) {
	    Map<String, Object> resp = new HashMap<>();
	    try {
	        boolean suc = service.insertKeyword(param);
	        resp.put("suc", suc);
	    } catch (Exception e) {
	        // MySQL/MariaDB Duplicate Key 에러코드: 1062
	        if (e.getCause() != null && e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
	            java.sql.SQLIntegrityConstraintViolationException sqlEx = (java.sql.SQLIntegrityConstraintViolationException) e.getCause();
	            if (sqlEx.getErrorCode() == 1062) {
	                resp.put("suc", false);
	                resp.put("code", 1062);
	                resp.put("message", "이미 등록된 키워드입니다.");
	                return resp;
	            }
	        }
	        resp.put("suc", false);
	        resp.put("message", e.getMessage());
	    }
	    return resp;
	}
	
	@PostMapping("/list")
	public Map<String, Object> listKeyword(){
		
		resp = new HashMap<String, Object>();
		
		ArrayList<SearchDTO> list = service.listKeyword();
		
		resp.put("list", list);
		
		return resp;
	}
	
	@PostMapping("/update")
	public Map<String, Object> updateKeyword(@RequestBody SearchDTO dto){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.updateKeyword(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
	
	@PostMapping("/delete")
	public Map<String, Object> delKeyword(@RequestBody SearchDTO dto){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.delKeyword(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
}
