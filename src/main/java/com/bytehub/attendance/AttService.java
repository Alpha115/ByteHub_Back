package com.bytehub.attendance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
@Slf4j
public class AttService {
    private final AttDAO dao;
    
    // 인증 실패 제한을 위한 메모리 저장소
    private final Map<String, FailureInfo> failureMap = new ConcurrentHashMap<>(); // ConcurrentHashMap은 동시 접근 해도 ㄱㅊ다고 해서 사용
    
    // 인증번호 저장소 (userId -> {inOtp, outOtp, expireTime})
    private final Map<String, OtpInfo> otpMap = new ConcurrentHashMap<>();
    
    // 실패 정보 클래스
    private static class FailureInfo {
        int failCount = 0;
        LocalDateTime lockUntil = null;
        LocalDateTime lastFailTime = null;
        
        boolean isLocked() {
            return lockUntil != null && LocalDateTime.now().isBefore(lockUntil);
        }
        
        void reset() {
            failCount = 0;
            lockUntil = null;
            lastFailTime = null;
        }
    }
    
    // 인증번호 정보 클래스
    private static class OtpInfo {
        String inOtp;
        String outOtp;
        LocalDateTime expireTime;
        
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expireTime);
        }
        
        boolean isValid(String inputCode, String mode) {
            if (isExpired()) {
                log.warn("인증번호가 만료되었습니다. expireTime: {}, 현재시간: {}", expireTime, LocalDateTime.now());
                return false;
            }
            if (inputCode == null) {
                log.warn("입력된 인증번호가 null입니다.");
                return false;
            }
            if ("in".equals(mode)) {
                boolean result = inputCode.equals(inOtp);
                log.info("출근 모드 검증 - 입력: {}, 저장된 출근용: {}, 결과: {}", inputCode, inOtp, result);
                return result;
            } else if ("out".equals(mode)) {
                boolean result = inputCode.equals(outOtp);
                log.info("퇴근 모드 검증 - 입력: {}, 저장된 퇴근용: {}, 결과: {}", inputCode, outOtp, result);
                return result;
            }
            log.warn("잘못된 모드: {}", mode);
            return false;
        }
    }
    
    // 인증 시도 전 잠금 상태 체크
    public Map<String, Object> chkAuthLock(String userId) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        
        FailureInfo info = failureMap.get(userId);
        if (info != null && info.isLocked()) {
            long remainingMinutes = java.time.Duration.between(LocalDateTime.now(), info.lockUntil).toMinutes();
            result.put("locked", true);
            result.put("message", remainingMinutes + "분 후 다시 시도해주세요.");
            result.put("remainingMinutes", remainingMinutes);
        } else {
            result.put("locked", false);
        }
        
        return result;
    }
    
    // 인증 실패 처리
    public void handleAuthFailure(String userId) {
        FailureInfo info = failureMap.computeIfAbsent(userId, k -> new FailureInfo());
        
        // 5분 이내 실패가 아니면 카운트 리셋
        if (info.lastFailTime != null && 
            java.time.Duration.between(info.lastFailTime, LocalDateTime.now()).toMinutes() >= 5) {
            info.reset();
        }
        
        info.failCount++;
        info.lastFailTime = LocalDateTime.now();
        
        // 5회 실패 시 10분 잠금
        if (info.failCount >= 5) {
            info.lockUntil = LocalDateTime.now().plusMinutes(10);
            log.warn("사용자 {}의 인증이 10분간 잠금되었습니다. ({}회 실패)", userId, info.failCount);
        }
        
        log.info("사용자 {} 인증 실패 - 현재 {}회 실패", userId, info.failCount);
    }
    
    // 인증 성공 처리
    public void handleAuthSuccess(String userId) {
        // 성공 시 실패 정보 완전 삭제
        failureMap.remove(userId);
        log.info("사용자 {} 인증 성공 - 실패 정보 초기화", userId);
    }
    
    // 인증번호 저장
    public void saveOtp(String userId, String inOtp, String outOtp) {
        OtpInfo otpInfo = new OtpInfo();
        otpInfo.inOtp = inOtp;
        otpInfo.outOtp = outOtp;
        otpInfo.expireTime = LocalDateTime.now().plusMinutes(10); // 10분 유효
        otpMap.put(userId, otpInfo);
        log.info("사용자 {} 인증번호 저장 - 출근용: {}, 퇴근용: {}", userId, inOtp, outOtp);
    }
    
    // 인증번호 검증
    public boolean verifyOtp(String userId, String inputCode, String mode) {
        log.info("=== 인증번호 검증 시작 ===");
        log.info("사용자 ID: {}", userId);
        log.info("입력된 인증번호: {}", inputCode);
        log.info("모드: {}", mode);
        
        OtpInfo otpInfo = otpMap.get(userId);
        if (otpInfo == null) {
            log.warn("사용자 {}의 저장된 인증번호가 없습니다.", userId);
            log.info("현재 저장된 모든 인증번호: {}", otpMap.keySet());
            return false;
        }
        
        log.info("저장된 인증번호 정보 - 출근용: {}, 퇴근용: {}, 만료시간: {}", 
                otpInfo.inOtp, otpInfo.outOtp, otpInfo.expireTime);
        
        boolean isValid = otpInfo.isValid(inputCode, mode);
        log.info("인증번호 검증 결과: {}", isValid);
        
        if (isValid) {
            // 성공 시 인증번호 삭제 (1회 사용)
            otpMap.remove(userId);
            log.info("사용자 {} 인증번호 사용 완료 - 삭제됨", userId);
        }
        
        log.info("=== 인증번호 검증 완료 ===");
        return isValid;
    }
    
    // 만료된 인증번호 정리
    public void cleanupExpiredOtps() {
        otpMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    // 디버깅용: 저장된 인증번호 확인
    public Map<String, Object> getDebugOtpInfo(String userId) {
        Map<String, Object> result = new HashMap<>();
        OtpInfo otpInfo = otpMap.get(userId);
        if (otpInfo != null) {
            result.put("success", true);
            result.put("inOtp", otpInfo.inOtp);
            result.put("outOtp", otpInfo.outOtp);
            result.put("expireTime", otpInfo.expireTime);
            result.put("isExpired", otpInfo.isExpired());
        } else {
            result.put("success", false);
            result.put("msg", "저장된 인증번호가 없습니다.");
            result.put("allKeys", otpMap.keySet());
        }
        return result;
    }

    // 출퇴근 기록 생성 
    public int insertAttendance(AttDTO dto) {
        return dao.insertAttendance(dto);
    }

    // 인증 기록 생성
    public int insertAttHistory(AttHistoryDTO dto) {
        return dao.insertAttHistory(dto);
    }
    
    // 출퇴근 기록 수정

    public int attUpdate(AttDTO dto) {
        return dao.attUpdate(dto);
    }

    // 출근/퇴근/지각/조퇴 내역 조회 기능
    public List<AttDTO> attList(String user_id) {
        return dao.attList(user_id);
    }
    
    // 월별 출근/퇴근/지각/조퇴 내역 조회 기능
    public List<AttDTO> monthlyList(String user_id, String yearMonth) {
		return dao.monthlyList(user_id, yearMonth);
	}

    // 전체 직원 근태 조회 기능
    public List<AttDTO> attListAll() {
        return dao.attListAll();
    }

    // 특정 출퇴근 기록 조회
    public AttDTO attDetail(int att_idx) {
        return dao.attDetail(att_idx);
    }

    // 출/퇴근 시간 설정 기능 -- 출퇴근 기준 시간 생성
	public int createAttSetting(AttSettingDTO dto) {
		return dao.createAttSetting(dto);
	}

    // 출/퇴근 시간 설정 기능 -- 출퇴근 기준 시간 수정
	public int updateAttSetting(AttSettingDTO dto) {
		return dao.updateAttSetting(dto);
	}

    // 출/퇴근 시간 설정 기능 -- 현재 적용되는 기준 시간 조회 (가장 최근 한 줄)
    public AttSettingDTO getAttSetting() {
        return dao.getAttSetting();
    }

	// 근태 통계
	public List<Map<String, Object>> attStat(String user_id) {
		return dao.attStat(user_id);
	}

	// 전체 직원 근태 통계
	public List<Map<String, Object>> attStatAll() {
		return dao.attStatAll();
	}

    // 최근 30일 출근/퇴근/지각/조퇴 내역 조회 기능
    public List<AttDTO> recent30days(String user_id) {
        return dao.recent30days(user_id);
    }
	
	
}
