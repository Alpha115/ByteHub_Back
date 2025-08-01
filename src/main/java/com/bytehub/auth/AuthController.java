package com.bytehub.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	Map<String, Object> resp=null;
	private final AuthService service;
	
	// 권한부여 패널 함수입니다.
	@PostMapping("/grant")
	public Map<String, Object> grant(@RequestBody ArrayList<Map<String, Object>> info){
		
		resp=new HashMap<String, Object>();
		boolean success=service.grant(info);
		resp.put("success", success);
		return resp;
	}
	
	// 관리자 페이지에서 user_id가 가진 권한을 조회합니다.
	@GetMapping("/grant/{user_id}")
	public Map<String, Object> grantId(@PathVariable String user_id){
		resp=new HashMap<String, Object>();
		resp.put("auth_list", service.grantId(user_id));
		return resp;
	}
	
	// 관리패널 접근권한 식별
	@GetMapping("/paeneol/{user_id}")
	public Map<String, Object> paeneol(@PathVariable String user_id,
			@RequestHeader Map<String, String> header){
		resp=new HashMap<String, Object>();
		String token = header.get("authorization");
		Map<String, Object> payload = JwtUtils.readToken(token);
		String loginId = (String) payload.get("id");
		
		if (!loginId.equals("") && loginId.equals(user_id)) {
			resp.put("my_auth", service.grantId(user_id));
		}
		return resp;
	}

}






/* grant*/

//auth(ArrayList): [
//              	{
//              		user_id: 'user01',
//              		권한 대상 타입: typeExample,
//              		idx: nn,
//              		권한: read or write
//              		체크: boolean
//              	},
//              	{
//              		user_id: 'user01',
//              		권한 대상 타입: typeExample,
//              		idx: nn,
//              		권한: read or write
//              		체크: boolean
//              	},
//              	{
//              		user_id: 'user01',
//              		권한 대상 타입: typeExample,
//              		idx: nn,
//              		권한: read or write
//              		체크: boolean
//              	},
//              ]