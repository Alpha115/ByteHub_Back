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

    // 32바이트(256비트) 이상 안전한 시크릿 키
    private static final String SECRET_KEY = "bytehub_super_secret_key_1234567890_ABCDEFGH";

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

            // JWT 토큰 생성
            String jwt = io.jsonwebtoken.Jwts.builder()
                    .setSubject(member.getUser_id())
                    .claim("name", member.getName())
                    .claim("email", member.getEmail())
                    .setIssuedAt(new java.util.Date())
                    .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                    .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                    .compact(); // signWith 는 jwt 토큰 위변조 방지(=서명)

            resp.put("success", true);
            resp.put("msg", "회원가입 성공");
            resp.put("user_id", member.getUser_id());
            resp.put("token", jwt);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("msg", "회원가입 중 오류 발생: " + e.getMessage());
        }
        return resp;
    }
}
