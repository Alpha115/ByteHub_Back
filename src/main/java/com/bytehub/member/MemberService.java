package com.bytehub.member;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MemberService {
	
	@Autowired MemberDAO dao;

	public boolean login(Map<String, String> info) {
		// 사용자 정보 조회
		MemberDTO member = dao.getMemberById(info.get("id"));
		if (member == null) {
			return false; // 사용자가 존재하지 않음
		}
		
		// BCrypt로 비밀번호 비교
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(info.get("password"), member.getPassword());
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
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(rawPassword);

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
}
