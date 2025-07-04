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
    public Map<String, Object> login(@RequestBody Map<String, String> info, HttpSession session){
        Map<String, Object> result = new HashMap<>();
        boolean success = service.login(info);
        
        if (success) {
            // 세션에 로그인 정보 저장 (LoginChecker가 요구하는 부분)
            session.setAttribute("loginId", info.get("id"));
            
            String token = JwtUtils.getToken("id", info.get("id"));
            result.put("token", token);
            result.put("success", success);
        }
        
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
}
