package com.bytehub.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MypageService {

    @Autowired
    private MemberDAO dao;
    
    // BCryptPasswordEncoder 직접 생성
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 사용자 정보 조회
    public MemberDTO getMemberInfo(String user_id) {
        log.info("Service: 사용자 정보 조회 - user_id: {}", user_id);
        
        try {
            MemberDTO member = dao.getMemberById(user_id);
            
            if (member != null) {
                log.info("Service: 사용자 정보 조회 성공 - name: {}", member.getName());
            } else {
                log.info("Service: 사용자를 찾을 수 없음 - user_id: {}", user_id);
            }
            
            return member;
        } catch (Exception e) {
            log.error("Service: 사용자 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    // 비밀번호 검증
    public boolean verifyPassword(String userId, String password) {
        log.info("Service: 비밀번호 검증 - user_id: {}", userId);
        
        try {
            MemberDTO member = dao.getMemberById(userId);
            
            if (member == null) {
                log.info("Service: 사용자를 찾을 수 없음 - user_id: {}", userId);
                return false;
            }
            
            // BCrypt로 비밀번호 검증
            boolean isValid = encoder.matches(password, member.getPassword());
            log.info("Service: 비밀번호 검증 결과: {}", isValid);
            
            return isValid;
        } catch (Exception e) {
            log.error("Service: 비밀번호 검증 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // 회원 정보 수정
    public boolean updateMemberInfo(String userId, String email, String newPassword) {
        log.info("Service: 회원 정보 수정 - user_id: {}", userId);
        
        try {
            MemberDTO member = dao.getMemberById(userId);
            
            if (member == null) {
                log.info("Service: 사용자를 찾을 수 없음 - user_id: {}", userId);
                return false;
            }
            
            // 수정할 정보 설정
            if (email != null && !email.trim().isEmpty()) {
                member.setEmail(email);
            }
            
            // 새 비밀번호가 제공된 경우 암호화하여 설정
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String encodedPassword = encoder.encode(newPassword);
                member.setPassword(encodedPassword);
                log.info("Service: 비밀번호 변경 완료");
            }
            
            // DB 업데이트
            int result = dao.updateMember(member);
            boolean success = result > 0;
            
            log.info("Service: 회원 정보 수정 결과: {}", success);
            return success;
            
        } catch (Exception e) {
            log.error("Service: 회원 정보 수정 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }
}
