package com.bytehub.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
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
}
