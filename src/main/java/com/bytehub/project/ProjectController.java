package com.bytehub.project;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/project")
public class ProjectController {
	
	Map<String, Object> resp=null;
	private final ProjectService service;
	
	@PostMapping("/create")
	public Map<String, Object> create(@RequestBody ProjectInsert info){
		resp=new HashMap<String, Object>();
		boolean success=service.create(info);
		resp.put("success", success);
		return resp;
	}
	

}


// project와 file 정보 묶음
class ProjectInsert{
	
	// 아니이걸 퍼블릭으로선언해야 된다고?
	public ProjectDTO proj;
	public int file_idx[];	// file_idx는 여러개가 가능
	
}