package com.bytehub.schedule;

import java.util.ArrayList;
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

//	// 일정(1일)일의 내용을 조회합니다.(일정정보, 해당날짜 일정 총 갯수)
//	@GetMapping("/day/{date}")
//	public Map<String, Object> day(@PathVariable Date date) {
//		resp = new HashMap<String, Object>();
//		// 아직하지말아봐
//		return resp;
//	}

	// 일정을 입력합니다.
	@PostMapping("/insert")
	public Map<String, Object> insert(@RequestBody ScdDTO info) {
		resp = new HashMap<String, Object>();
		resp.put("success", svc.insert(info));
		return resp;
	}

	// 일정을 수정합니다.
	@PostMapping("/edit")
	public Map<String, Object> edit(@RequestBody ScdDTO info) {
		resp = new HashMap<String, Object>();
		resp.put("success", svc.edit(info));
		return resp;
	}
	
	// 일정을 삭제합니다. (type가 있는 경우)
	@GetMapping("/del/{scd_type}/{type_idx}")
	public Map<String, Object> delete(@PathVariable String scd_type, @PathVariable int type_idx){
		resp = new HashMap<String, Object>();
		resp.put("success", svc.delete(type_idx, scd_type));
		return resp;
	}
	
	@GetMapping("/del/{subject}")
	public Map<String, Object> deleteBySub(@PathVariable String subject){
		resp = new HashMap<String, Object>();
		resp.put("success", svc.delete(subject));
		return resp;
	}

	// 일정 전체를 불러오는 기능 아싸 페이지네이션 없어도 된대
	@GetMapping("/total")
	public Map<String, Object> total() {
		resp = new HashMap<String, Object>();
		ArrayList<ScdDTO> list = svc.total();
		resp.put("scd_list", list);
		return resp;
	}
	

	


}
