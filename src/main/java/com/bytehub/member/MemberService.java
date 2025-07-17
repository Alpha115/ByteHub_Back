package com.bytehub.member;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MemberService {
	
	@Autowired MemberDAO dao;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public boolean login(Map<String, String> info) {
		log.info("Service: Login attempt for user: {}", info.get("id"));
		
		// 사용자 정보 조회
		MemberDTO member = dao.getMemberById(info.get("id"));
		if (member == null) {
			log.info("Service: User not found: {}", info.get("id"));
			return false; // 사용자가 존재하지 않음
		}
		
		// encoder.matches([입력받은 pw], [db에 저장된 pw])
		boolean passwordMatch = encoder.matches(info.get("password"), member.getPassword());
		log.info("Service: Password match result: {}", passwordMatch);
		
		return passwordMatch;
	}

    public String findUserId(String name, String email) {
        log.info("Service: Finding user ID for name: {}, email: {}", name, email);
        String userId = dao.findUserId(name, email);
        log.info("Service: Found user ID: {}", userId);
        return userId;
    }

    public boolean overlay(String id) {
        log.info("Service: " + id + " 중복체크");
        int cnt = dao.countById(id);
        return cnt == 0;
    }

    public Map<String, Object> join(Map<String, Object> param) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String rawPassword = (String) param.get("password");
            String encodedPassword = encoder.encode(rawPassword);

            MemberDTO member = new MemberDTO();
            member.setUser_id((String) param.get("user_id"));
            member.setFile_idx(param.get("file_idx") == null ? 0 : (int) param.get("file_idx"));
            member.setDept_idx(param.get("dept_idx") == null ? 0 : (int) param.get("dept_idx"));
            member.setLv_idx(param.get("lv_idx") == null ? 0 : (int) param.get("lv_idx"));
            member.setPassword(encodedPassword);
            member.setName((String) param.get("name"));
            member.setEmail((String) param.get("email"));
            member.setGender((String) param.get("gender"));
            member.setHire_date(java.sql.Date.valueOf((String) param.get("hire_date")));

            // DB 저장
            dao.insertMember(member);

            resp.put("success", true);
            resp.put("msg", "회원가입 성공");
            resp.put("user_id", member.getUser_id());
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("msg", "회원가입 중 오류 발생: " + e.getMessage());
        }
        return resp;
    }

    // 아이디와 이메일로 사용자 확인
    public boolean checkUserByIdAndEmail(String userId, String email) {
        log.info("Service: Checking user by ID: {} and email: {}", userId, email);
        MemberDTO member = dao.getMemberById(userId);
        if (member != null && email.equals(member.getEmail())) {
            log.info("Service: User found and email matches");
            return true;
        }
        log.info("Service: User not found or email doesn't match");
        return false;
    }

    // 비밀번호 업데이트
    public boolean updatePassword(String userId, String newPassword) {
        log.info("Service: Updating password for user: {}", userId);
        try {
            String encodedPassword = encoder.encode(newPassword);
            int result = dao.updatePassword(userId, encodedPassword);
            boolean success = result > 0;
            log.info("Service: Password update result: {}", success);
            return success;
        } catch (Exception e) {
            log.info("Service: Password update failed: {}", e.getMessage(), e);
            return false;
        }
    }

	public ArrayList<MemberDTO> memberList(MemberDTO dto) {
		return dao.memberList(dto);
	}
	
	// 이메일 확인
	public String findEmail(String userId) {
		 return dao.findEmail(userId);
	
	}

	public boolean memberUpdate(MemberDTO dto) {
		int row = dao.memberUpdate(dto);
		return row > 0 ? true : false;
	}

	public boolean memberDelete(MemberDTO dto) {
		int row = dao.memberDelete(dto);
		return row > 0 ? true : false;
	}

	// 사용자 정보 조회 (부서명,이름)
	public MemberDTO memberInfo(String user_id) {
		return dao.memberInfo(user_id);
	}

	public ArrayList<Map<String, Object>> depts() {
		return dao.depts();
	}


	

}
