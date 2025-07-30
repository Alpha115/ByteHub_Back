package com.bytehub.member;

import org.springframework.web.bind.annotation.*;

import com.bytehub.notification.NotiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bytehub.utils.JwtUtils;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
  
    private final MemberService service;
    private final NotiService notiService;

    Map<String, Object> result = null;

    
    // 회원가입
    @PostMapping("/join")
    public Map<String, Object> join(@RequestBody Map<String, Object> param) {
        try {
            // dept_idx와 lv_idx가 없거나 0이면 기본값 설정
            if (param.get("dept_idx") == null || (Integer) param.get("dept_idx") == 0) {
                param.put("dept_idx", 99); // 기본 부서 ID
            }
            if (param.get("lv_idx") == null || (Integer) param.get("lv_idx") == 0) {
                param.put("lv_idx", 7); // 기본 직급 ID
            }
            
            return service.join(param);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("msg", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
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
    
    @PostMapping("/list")
    public Map<String, Object> memberList(MemberDTO dto){
        Map<String, Object> result = new HashMap<>();
        ArrayList<MemberDTO> list = service.memberList(dto);
    	result.put("list", list);
    	return result;
    }
    
    @PostMapping("/list/update")
    public Map<String, Object> memberUpdate(@RequestBody MemberDTO dto){
        Map<String, Object> result = new HashMap<>();
        boolean suc = service.memberUpdate(dto);
        
        if (suc) {
            result.put("success", true);
            result.put("message", "멤버 정보가 성공적으로 업데이트되었습니다.");
            log.info("멤버 정보 업데이트 성공: {}", dto.getUser_id());
        } else {
            result.put("success", false);
            result.put("message", "멤버 정보 업데이트에 실패했습니다.");
            log.info("멤버 정보 업데이트 실패: {}", dto.getUser_id());
        }
        
        result.put("suc", suc);
        return result;
    }
    
    @PostMapping("/list/delete")
    public Map<String, Object> memberDelete(@RequestBody MemberDTO dto){
        Map<String, Object> result = new HashMap<>();
        boolean suc = service.memberDelete(dto);
        result.put("suc",suc);
        return result;
    }
}
