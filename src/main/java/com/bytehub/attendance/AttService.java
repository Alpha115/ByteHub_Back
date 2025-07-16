package com.bytehub.attendance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AttService {
    private final AttDAO dao;

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

    // 출/퇴근 시간 설정 기능 -- 현재 적용되는 기준 시간 조회
	public AttSettingDTO getAttSetting(String user_id) {
		return dao.getAttSetting(user_id);
	}

	// 근태 통계
	public List<Map<String, Object>> attStat(String user_id) {
		return dao.attStat(user_id);
	}
	
	// 결석 자동 처리
	public int processAbsence(LocalDate targetDate) {
		try {
			// 특정 날짜에 출근 기록이 없는 재직 중인 직원들 조회
			List<String> absentEmployees = dao.getAbsentEmployees(targetDate);
			
			if (absentEmployees.isEmpty()) {
				log.info("{}에 결석 처리할 직원이 없습니다.", targetDate);
				return 0;
			}
			
			// 결석 기록 일괄 생성
			int result = dao.insertAbsenceRecords(targetDate, absentEmployees);
			log.info("{}에 {}명의 직원을 결석 처리했습니다.", targetDate, absentEmployees.size());
			
			return result;
			
		} catch (Exception e) {
			log.error("결석 자동 처리 중 오류 발생: ", e);
			throw e;
		}
	}
	
	// 전날 결석 자동 처리 (배치용)
	public int processYesterdayAbsence() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		return processAbsence(yesterday);
	}
	
	// 개별 결석 처리
	public boolean processSingleAbsence(String userId, LocalDate targetDate) {
		try {
			// 이미 해당 날짜에 기록이 있는지 확인
			int existingRecords = dao.checkAttendanceExists(userId, targetDate);
			
			if (existingRecords > 0) {
				log.warn("사용자 {}의 {}에 이미 출근 기록이 존재합니다.", userId, targetDate);
				return false; // 이미 기록이 있으면 처리하지 않음
			}
			
			// 결석 기록 생성
			int result = dao.insertSingleAbsenceRecord(userId, targetDate);
			
			if (result > 0) {
				log.info("사용자 {}의 {}를 결석으로 처리했습니다.", userId, targetDate);
				return true;
			} else {
				log.error("사용자 {}의 {} 결석 처리에 실패했습니다.", userId, targetDate);
				return false;
			}
			
		} catch (Exception e) {
			log.error("개별 결석 처리 중 오류 발생: ", e);
			return false;
		}
	}
}
