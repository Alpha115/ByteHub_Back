package com.bytehub.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/mypage")
public class MypageController {

    @Autowired
    private MypageService service;

    // 내 정보 조회
    @GetMapping("/info/{user_id}")
    public Map<String, Object> getMyInfo(@PathVariable String user_id) {
        log.info("내 정보 조회 요청 - 사용자 ID: {}", user_id);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            MemberDTO member = service.getMemberInfo(user_id);
            
            if (member != null) {
                result.put("success", true);
                result.put("message", "내 정보 조회 성공");
                result.put("data", member);
            } else {
                result.put("success", false);
                result.put("message", "해당 사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("내 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "내 정보 조회 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // 비밀번호 검증
    @PostMapping("/verify-password")
    public Map<String, Object> verifyPassword(@RequestBody Map<String, String> request) {
        log.info("비밀번호 검증 요청 - 사용자 ID: {}", request.get("user_id"));
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String userId = request.get("user_id");
            String password = request.get("password");
            
            boolean isValid = service.verifyPassword(userId, password);
            
            if (isValid) {
                result.put("success", true);
                result.put("message", "비밀번호가 확인되었습니다.");
            } else {
                result.put("success", false);
                result.put("message", "비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            log.error("비밀번호 검증 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "비밀번호 검증 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // 회원 정보 수정
    @PutMapping("/update")
    public Map<String, Object> updateMemberInfo(@RequestBody Map<String, Object> request) {
        log.info("회원 정보 수정 요청 - 사용자 ID: {}", request.get("user_id"));
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String userId = (String) request.get("user_id");
            String email = (String) request.get("email");
            String newPassword = (String) request.get("new_password");
            
            boolean updateSuccess = service.updateMemberInfo(userId, email, newPassword);
            
            if (updateSuccess) {
                result.put("success", true);
                result.put("message", "회원 정보가 성공적으로 수정되었습니다.");
            } else {
                result.put("success", false);
                result.put("message", "회원 정보 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("회원 정보 수정 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "회원 정보 수정 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
