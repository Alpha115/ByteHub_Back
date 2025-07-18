package com.bytehub.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            
            // 현재 시간
            LocalDateTime now = LocalDateTime.now();
            
            if ("in".equals(mode)) {
                // 출근 처리
                att.setIn_time(now);
                att.setAtt_type(determineInAttendanceStatus(userId, now));
            } else {
                // 퇴근 처리
                att.setOut_time(now);
                att.setAtt_type(determineOutAttendanceStatus(userId, now));
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
    
    /**
     * 출근 시 근태 상태 결정
     */
    private String determineInAttendanceStatus(String userId, LocalDateTime currentTime) {
        try {
            log.info("출근 상태 결정 시작 - userId: {}, currentTime: {}", userId, currentTime);
            
            // (모두에게 동일한) 최신 출퇴근 설정 조회
            AttSettingDTO setting = svc.getAttSetting();
            log.info("공용 설정 조회 결과: {}", setting);
            
            // 설정이 없으면 기본값 사용 (09:00)
            LocalTime standardInTime = LocalTime.of(9, 0);
            int termMinutes = 30; // 기본 30분 유효시간
            
            if (setting != null && setting.getSet_in_time() != null) {
                standardInTime = setting.getSet_in_time().toLocalTime();
                termMinutes = setting.getTerm();
            }
            
            LocalTime currentInTime = currentTime.toLocalTime();
            log.info("기준 출근시간: {}, 유효시간: {}분, 현재시간: {}", standardInTime, termMinutes, currentInTime);
            
            // 유효한 출근 시간 범위 계산
            LocalTime earliestAllowedTime = standardInTime.minusMinutes(termMinutes); // 예: 08:50
            LocalTime latestAllowedTime = standardInTime.plusMinutes(termMinutes);   // 예: 09:10
            log.info("유효 출근 범위: {} ~ {}", earliestAllowedTime, latestAllowedTime);
            
            // 유효 시간 범위 내에서만 정상출근 인정
            boolean isInRange = !currentInTime.isBefore(earliestAllowedTime) && !currentInTime.isAfter(latestAllowedTime);
            log.info("범위 내 여부: {}", isInRange);
            
            if (isInRange) {
                log.info("결과: 정상출근");
                return "정상출근"; // 08:50 ~ 09:10 사이
            } else {
                log.info("결과: 지각");
                return "지각"; // 그 외 모든 시간 (새벽, 너무 늦은 시간 포함)
            }
            
        } catch (Exception e) {
            log.error("출근 상태 결정 중 오류: ", e);
            return "지각"; // 오류 시 지각으로 변경
        }
    }
    
    /**
     * 퇴근 시 근태 상태 결정
     */
    private String determineOutAttendanceStatus(String userId, LocalDateTime currentTime) {
        try {
            // (모두에게 동일한) 최신 출퇴근 설정 조회
            AttSettingDTO setting = svc.getAttSetting();
            
            // 설정이 없으면 기본값 사용 (18:00)
            LocalTime standardOutTime = LocalTime.of(18, 0);
            
            if (setting != null && setting.getSet_out_time() != null) {
                standardOutTime = setting.getSet_out_time().toLocalTime();
            }
            
            LocalTime currentOutTime = currentTime.toLocalTime();
            
            // 기준 퇴근 시간 기준으로 상태 결정
            if (currentOutTime.isAfter(standardOutTime) || currentOutTime.equals(standardOutTime)) {
                return "정상퇴근";
            } else {
                return "조퇴";
            }
            
        } catch (Exception e) {
            log.error("퇴근 상태 결정 중 오류: ", e);
            return "정상퇴근"; // 오류 시 기본값
        }
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
    public Map<String, Object> getCurrentSetting() {
        Map<String, Object> result = new HashMap<>();
        try {
            AttSettingDTO setting = svc.getAttSetting();
            result.put("success", true);
            result.put("data", setting);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "기준 시간 조회 실패: " + e.getMessage());
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

    // 결석 자동 처리 API (관리자용)
    @PostMapping("/attendance/process-absence")
    public Map<String, Object> processAbsence(
            @RequestBody Map<String, Object> request,
            @RequestHeader Map<String, String> header) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // JWT 토큰 검증
            String token = header.get("authorization");
            Map<String, Object> tokenData = JwtUtils.readToken(token);
            String loginId = (String) tokenData.get("id");
            
            if (loginId == null) {
                result.put("success", false);
                result.put("msg", "인증 실패");
                return result;
            }
            
            // 관리자 권한 체크 (필요시 추가)
            
            String targetDateStr = (String) request.get("targetDate");
            if (targetDateStr != null) {
                // 특정 날짜 처리
                LocalDate targetDate = LocalDate.parse(targetDateStr);
                int processedCount = svc.processAbsence(targetDate);
                result.put("success", true);
                result.put("msg", processedCount + "명의 직원을 결석 처리했습니다.");
                result.put("processedCount", processedCount);
            } else {
                // 전날 자동 처리
                int processedCount = svc.processYesterdayAbsence();
                result.put("success", true);
                result.put("msg", "전날 " + processedCount + "명의 직원을 결석 처리했습니다.");
                result.put("processedCount", processedCount);
            }
            
        } catch (Exception e) {
            log.error("결석 처리 실패: " + e.getMessage());
            result.put("success", false);
            result.put("msg", "결석 처리 중 오류가 발생했습니다.");
        }
        
        return result;
    }

    // 개별 결석 처리 API (관리자용)
    @PostMapping("/attendance/process-single-absence")
    public Map<String, Object> processSingleAbsence(
            @RequestBody Map<String, Object> request,
            @RequestHeader Map<String, String> header) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // JWT 토큰 검증
            String token = header.get("authorization");
            Map<String, Object> tokenData = JwtUtils.readToken(token);
            String loginId = (String) tokenData.get("id");
            
            if (loginId == null) {
                result.put("success", false);
                result.put("msg", "인증 실패");
                return result;
            }
            
            // 요청 파라미터 검증
            String targetUserId = (String) request.get("userId");
            String targetDateStr = (String) request.get("targetDate");
            
            if (targetUserId == null || targetUserId.trim().isEmpty()) {
                result.put("success", false);
                result.put("msg", "사용자 ID가 필요합니다.");
                return result;
            }
            
            if (targetDateStr == null || targetDateStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("msg", "날짜가 필요합니다.");
                return result;
            }
            
            LocalDate targetDate = LocalDate.parse(targetDateStr);
            boolean success = svc.processSingleAbsence(targetUserId, targetDate);
            
            if (success) {
                result.put("success", true);
                result.put("msg", targetUserId + "님의 " + targetDate + "를 결석 처리했습니다.");
            } else {
                result.put("success", false);
                result.put("msg", "이미 해당 날짜에 출근 기록이 존재하거나 처리에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("개별 결석 처리 실패: " + e.getMessage());
            result.put("success", false);
            result.put("msg", "개별 결석 처리 중 오류가 발생했습니다.");
        }
        
        return result;
    }

    // 팀 근태 확인 기능 (연차만) 이거는 권한되고 나서??

    // 시간 되면 인증번호 시도 제한 및 잠금 기능;
    
   

}
