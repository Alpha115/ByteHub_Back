package com.bytehub.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/level")
public class LevelController {

	private final LevelService service;
	
	Map<String, Object> resp=null;
	
	@PostMapping("/list")
	public Map<String, Object> lvList(LevelDTO dto){
		
		resp = new HashMap<String, Object>();
		
		ArrayList<LevelDTO> list = service.lvList(dto);
		
		resp.put("list", list);
		
		return resp;
	}
	
	@PostMapping("/insert")
	public Map<String, Object> lvInsert(@RequestBody LevelDTO dto){
		
		resp = new HashMap<String, Object>();

		boolean suc = service.lvInsert(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
	
	@PostMapping("/update")
	public Map<String, Object> lvUpdate(@RequestBody LevelDTO dto){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.lvUpdate(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
	
	@PostMapping("/delete")
	public Map<String, Object> lvDelete(@RequestBody LevelDTO dto){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.lvDelete(dto);
		
		resp.put("suc", suc);
		
		return resp;
		
	}
	
}
	