package com.bytehub.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

import com.bytehub.utils.JwtUtils;
import javax.servlet.http.HttpSession;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService service;

    Map<String, Object> result = null;

    
    // 회원가입
    @PostMapping("/join")
    public Map<String, Object> join(@RequestBody Map<String, Object> param, HttpSession session) {
        Map<String, Object> result = service.join(param);
        
        if ((Boolean) result.get("success")) {
            // 회원가입 성공 시 자동 로그인
            session.setAttribute("loginId", param.get("user_id"));
        }
        
        return result;
    }

    // 로그인
    @PostMapping(value="/login")
	public Map<String, Object> login(@RequestBody Map<String, String> info){
		
		result = new HashMap<String, Object>();
		boolean success = service.login(info);
		
		if (success) {
			String token = JwtUtils.getToken("id", info.get("id"));
			result.put("token", token);
			
		}
		result.put("success", success); 
		return result;
	}


    // 아이디 중복체크
    @GetMapping("/overlay/{id}")
    public Map<String, Object> overlay(@PathVariable String id) {
        log.info(id + " 중복체크");
        Map<String, Object> result = new HashMap<>();
        boolean success = service.overlay(id);
        result.put("use", success);
        return result;
    }

    // 아이디 찾기
    @PostMapping("/find-id")
    public Map<String, Object> findUserId(@RequestBody Map<String, String> info) {
        log.info("아이디 찾기 요청 - 이름: {}, 이메일: {}", info.get("name"), info.get("email"));
        Map<String, Object> result = new HashMap<>();
        
        String userId = service.findUserId(info.get("name"), info.get("email"));
        
        if (userId != null && !userId.isEmpty()) {
            result.put("success", true);
            result.put("user_id", userId);
            result.put("message", "아이디를 찾았습니다: " + userId);
            log.info("아이디 찾기 성공: {}", userId);
        } else {
            result.put("success", false);
            result.put("message", "해당 정보로 등록된 아이디가 없습니다.");
            log.info("아이디 찾기 실패: 해당 정보 없음");
        }
        
        return result;
    }
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        session.invalidate(); // 세션 전체 무효화 (모든 세션 데이터 삭제)
        result.put("success", true);
        result.put("message", "로그아웃 되었습니다.");
        return result;
}
}
