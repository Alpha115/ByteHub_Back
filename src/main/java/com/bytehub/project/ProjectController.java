package com.bytehub.project;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	//프로젝트 상세보기 함수입니다.
	@GetMapping("/detail/{idx}")
	public Map<String, Object> detail(@PathVariable int idx){
		return service.detail(idx);
	}
	
	// 프로젝트 리스트 출력
	@GetMapping("/list")
	public Map<String, Object> list(){
		resp=new HashMap<String, Object>();
		resp.put("list", service.list());
		return resp;
	}
	
	// 프로젝트를 생성합니다.
	@PostMapping("/create")
	public Map<String, Object> create(@RequestBody ProjectData info){
		resp=new HashMap<String, Object>();
		boolean success=service.create(info);
		resp.put("success", success);
		return resp;
	}
	
	// 프로젝트를 수정합니다.
	// 멤버할당 들어가야돼 아
	@PutMapping("/edit")
	public Map<String, Object> edit(@RequestBody ProjectData info){
		resp=new HashMap<String, Object>();
		boolean success=service.edit(info);
		resp.put("success", success);
		return resp;
	}
	
	
	// 프로젝트를 삭제합니다.	<< d이거 아카이브로 하자는 제안
//	@GetMapping("/del/{project_idx}")
//	public Map<String, Object> delete(@PathVariable int project_idx){
//		resp=new HashMap<String, Object>();
//		resp.put("success", service.delete(project_idx));
//		return resp;
//	}

}


// project와 file 정보 묶음
class ProjectData{
	
	// 아니이걸 퍼블릭으로선언해야 된다고?
	public ProjectDTO proj;
	public int file_idx[];	// file_idx는 여러개가 가능
	public String user_id[];
	
}