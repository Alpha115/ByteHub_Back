package com.bytehub.attendance;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AttController {

    private final AttService service;
    
    // 출퇴근 기록 생성 및 인증 기록 생성

    @PostMapping("/attendance/verify")
    public Map<String, Object> verifyAttendance(@RequestBody Map<String, Object> req) {
        String userId = (String) req.get("user_id");
        String inputCode = (String) req.get("input_code");
        String expectedCode = (String) req.get("expected_code");
        String mode = (String) req.get("mode"); // "in" or "out"
        boolean success = inputCode != null && inputCode.equals(expectedCode);

        Integer attIdx = null;
        if (success) {
            // 출근/퇴근 기록 생성
            AttDTO att = new AttDTO();
            att.setUser_id(userId);
            att.setAtt_date(LocalDate.now());
            if ("in".equals(mode)) {
                att.setIn_time(LocalDateTime.now());
                att.setAtt_type("출근");
            } else {
                att.setOut_time(LocalDateTime.now());
                att.setAtt_type("퇴근");
            }
            service.insertAttendance(att);
            attIdx = att.getAtt_idx();
        }
        // 인증 히스토리 기록 (성공/실패 모두)
        AttHistoryDTO hist = new AttHistoryDTO();
        hist.setAtt_idx(success ? attIdx : null); // 성공 시에만 att_idx, 실패 시 null
        try {
            hist.setCert_no(Integer.parseInt(inputCode));
        } catch (Exception e) {
            hist.setCert_no(0);
        }
        hist.setCert_status(success);
        hist.setCert_time(LocalDateTime.now());
        service.insertAttHistory(hist);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "인증 성공" : "인증 실패");
        return result;
    }

    // 개인 근태 일정 조회 기능

    // 출근/퇴근/지각/조퇴 내역 조회 기능

    // 출/퇴근 시간 자동 기록 및 상태 분류 기능

    // 근태 통계 있어야 함;

    // 팀 근태 확인 기능 (연차만)

    // 시간 되면 인증번호 시도 제한 및 잠금 기능;

}
