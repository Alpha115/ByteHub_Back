package com.bytehub.department;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.member.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dept")
public class DeptController {

	private final DeptService service;
	
	Map<String, Object> resp=null;
	
	@PostMapping("/insert")
	public Map<String, Object> deptInsert(@RequestBody DeptDTO dto){
		
		resp = new HashMap<String, Object>();
		System.out.println("user_id in DTO: " + dto.getUser_id());
		boolean suc = service.deptInsert(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
	
	
	@PostMapping("/list")
	public Map<String, Object> deptList(DeptDTO dto, MemberDTO mem){

		resp = new HashMap<String, Object>();
		
		ArrayList<DeptDTO> list = service.deptList(dto);
		ArrayList<MemberDTO> member = service.memberList(mem);
		
		resp.put("list", list);
		resp.put("member", member);
		
		return resp;
	}
	
	@PostMapping("/update")
	public Map<String, Object> deptUpdate(@RequestBody DeptDTO dto){

		resp = new HashMap<String, Object>();
		
		boolean suc = service.deptUpdate(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
	
	@PostMapping("/delete")
	public Map<String, Object> deptDelete(@RequestBody DeptDTO dto){
		
		resp = new HashMap<String, Object>();
		
		boolean suc = service.deptDelete(dto);
		
		resp.put("suc", suc);
		
		return resp;
	}
}
