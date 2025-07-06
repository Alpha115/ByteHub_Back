package com.bytehub.chatbot;

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
	public Map<String, Object> insertKeyword(@RequestBody Map<String, Object> param){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.insertKeyword(param);
		
		resp.put("suc", suc);
		
		return resp;
	}

}
