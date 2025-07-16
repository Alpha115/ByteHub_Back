package com.bytehub.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.member.MemberDTO;
import com.bytehub.member.MemberService;
import com.bytehub.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AttController {

    private final AttService svc;
    private final MemberService msvc;

    
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
            svc.insertAttendance(att);
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
        svc.insertAttHistory(hist);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "인증 성공" : "인증 실패");
        return result;
    }
    
    
    // 관리자가 출퇴근 기록 수정 할 수 있어야 함
    
    @PutMapping("/attendance/update")
    public Map<String, Object> attUpdate (
    		@RequestBody AttDTO dto, // AttDTO로 변경
    		@RequestHeader Map<String, String> header){
    	
    	log.info("header : "+header); // 요청 헤더 로그 출력
    	Map<String, Object> result = new HashMap<>(); // 응답 데이터 저장용
    	
        String loginId = null;
        boolean login = false;
        
     // JWT 토큰에서 로그인 ID 추출 (try-catch로 JWT 오류 처리)
	    try {
	    	String token = header.get("authorization");
	    	Map<String, Object> tokenData = JwtUtils.readToken(token);
	    	loginId = (String) tokenData.get("id");
	    	login = true;
	    } catch (Exception e) {
	    	log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
	    	result.put("success", false);
	    	result.put("msg", "인증 실패");
	    	return result;
	    }
		
		// 출퇴근 기록 수정
		try {
			int updateResult = svc.attUpdate(dto);
			if (updateResult > 0) {
				result.put("success", true);
				result.put("msg", "출퇴근 기록이 수정되었습니다.");
			} else {
				result.put("success", false);
				result.put("msg", "수정할 기록을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			log.error("출퇴근 기록 수정 실패: " + e.getMessage());
			result.put("success", false);
			result.put("msg", "수정 중 오류가 발생했습니다.");
		}
	    
	    return result;
    	
    }
    

    // 출근/퇴근/지각/조퇴/결석 내역 조회 기능
    @GetMapping("/attendance/list")
    public Map<String, Object> attList(@RequestParam String user_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AttDTO> list = svc.attList(user_id);
            
            result.put("success", true);
            result.put("data", list);

        } catch (Exception e) {
            log.error("출퇴근 기록 조회 실패: " + e.getMessage());
            result.put("success", false);
            result.put("msg", "조회 중 오류가 발생했습니다.");
        }
        return result;
    }

    // 전체 직원 근태 조회
    @GetMapping("/attendance/list/all")
    public Map<String, Object> attListAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AttDTO> list = svc.attListAll();
            
            result.put("success", true);
            result.put("data", list);

        } catch (Exception e) {
            log.error("전체 출퇴근 기록 조회 실패: " + e.getMessage());
            result.put("success", false);
            result.put("msg", "조회 중 오류가 발생했습니다.");
        }
        return result;
    }

    // 특정 출퇴근 기록 조회
    @GetMapping("/attendance/detail")
    public Map<String, Object> attDetail(@RequestParam int att_idx) {
        Map<String, Object> result = new HashMap<>();
        try {
            AttDTO dto = svc.attDetail(att_idx);
            if (dto != null) {
                result.put("success", true);
                result.put("data", dto);
            } else {
                result.put("success", false);
                result.put("msg", "해당 기록을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("출퇴근 기록 상세 조회 실패: " + e.getMessage());
            result.put("success", false);
            result.put("msg", "조회 중 오류가 발생했습니다.");
        }
        return result;
    }

    // 출/퇴근 시간 설정 기능 -- 출퇴근 기준 시간 생성
    @PostMapping("/attendance/setting/create")
    public Map<String, Object> createAttSetting(@RequestBody AttSettingDTO dto) {
        Map<String, Object> result = new HashMap<>();
        try {
            int insertResult = svc.createAttSetting(dto);
            if (insertResult > 0) {
                result.put("success", true);
                result.put("msg", "기준 시간이 설정되었습니다.");
            } else {
                result.put("success", false);
                result.put("msg", "설정에 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "오류가 발생했습니다.");
        }
        return result;
    }
    
    // 출/퇴근 시간 설정 기능 -- 출퇴근 기준 시간 수정
    @PutMapping("/attendance/setting/update")
    public Map<String, Object> updateAttSetting(@RequestBody AttSettingDTO dto) {
        Map<String, Object> result = new HashMap<>();
        try {
            int updateResult = svc.updateAttSetting(dto);
            if (updateResult > 0) {
                result.put("success", true);
                result.put("msg", "기준 시간이 수정되었습니다.");
            } else {
                result.put("success", false);
                result.put("msg", "수정할 설정을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "수정 중 오류가 발생했습니다.");
        }
        return result;
    }
    
    // 출/퇴근 시간 설정 기능 -- 현재 적용되는 기준 시간 조회
    @GetMapping("/attendance/setting/current")
    public Map<String, Object> getCurrentSetting(@RequestParam String user_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            AttSettingDTO setting = svc.getAttSetting(user_id);
            if (setting != null) {
                result.put("success", true);
                result.put("data", setting);
            } else {
                result.put("success", false);
                result.put("msg", "설정된 기준 시간이 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 중 오류가 발생했습니다.");
        }
        return result;
    }

    // 근태 통계 있어야 함;
    @GetMapping("/attendance/stat")
    public Map<String, Object> attStat(@RequestParam String user_id){
        Map<String, Object> result = new HashMap<>();

        try {
        	// 근태 통계 조회
            List<Map<String, Object>> statList = svc.attStat(user_id);
            
            // 사용자 정보 조회 (부서명,이름)
            MemberDTO member = msvc.memberInfo(user_id);
            
            // 이름과 부서명만 추려서 내려주기
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("name", member.getName());
            userInfo.put("dept_name", member.getDept_name());
            
            result.put("success", true);
            result.put("data", statList);
            result.put("user_info", userInfo); // 부서명이랑 이름 가져오면 될 듯

        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "통계 조회 실패");
        }
        return result;
        
    }

    // 팀 근태 확인 기능 (연차만) 이거는 권한되고 나서??

    // 시간 되면 인증번호 시도 제한 및 잠금 기능;
    
   

}
