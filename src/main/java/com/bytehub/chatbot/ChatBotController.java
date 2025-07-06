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
	public Map<String, Object> insertKeyword(@RequestBody Map<String, Object> param){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.insertKeyword(param);
		
		resp.put("suc", suc);
		
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
