package com.bytehub.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService service;

    // 내 정보 조회 (토큰에서 userId 추출)
    @GetMapping("/info")
    public Map<String, Object> getMyInfo(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 토큰에서 userId 추출
            Map<String, Object> tokenData = JwtUtils.readToken(token);
            String userId = (String) tokenData.get("id");
            
            if (userId == null) {
                result.put("success", false);
                result.put("message", "유효하지 않은 토큰입니다.");
                return result;
            }
            
            log.info("내 정보 조회 요청 - 사용자 ID: {}", userId);
            
            MemberDTO member = service.getMemberInfo(userId);
            
            if (member != null) {
                result.put("success", true);
                result.put("message", "내 정보 조회 성공");
                result.put("data", member);
            } else {
                result.put("success", false);
                result.put("message", "해당 사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.info("내 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "내 정보 조회 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // 비밀번호 검증 (토큰에서 userId 추출)
    @PostMapping("/verify-password")
    public Map<String, Object> verifyPassword(@RequestHeader("Authorization") String token, 
                                             @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 토큰에서 userId 추출
            Map<String, Object> tokenData = JwtUtils.readToken(token);
            String userId = (String) tokenData.get("id");
            
            if (userId == null) {
                result.put("success", false);
                result.put("message", "유효하지 않은 토큰입니다.");
                return result;
            }
            
            log.info("비밀번호 검증 요청 - 사용자 ID: {}", userId);
            
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
            log.info("비밀번호 검증 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "비밀번호 검증 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // 회원 정보 수정 (토큰에서 userId 추출)
    @PutMapping("/update")
    public Map<String, Object> updateMemberInfo(@RequestHeader("Authorization") String token,
                                               @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 토큰에서 userId 추출
            Map<String, Object> tokenData = JwtUtils.readToken(token);
            String userId = (String) tokenData.get("id");
            
            if (userId == null) {
                result.put("success", false);
                result.put("message", "유효하지 않은 토큰입니다.");
                return result;
            }
            
            log.info("회원 정보 수정 요청 - 사용자 ID: {}", userId);
            
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
            log.info("회원 정보 수정 중 오류 발생: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "회원 정보 수정 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
