package com.bytehub.attendance;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttDAO {
	
	// 출퇴근 기록 생성 
    int insertAttendance(AttDTO dto);
    
    // 인증 기록 생성
    int insertAttHistory(AttHistoryDTO dto);

    // 출퇴근 기록 수정
    int attUpdate(AttDTO dto);
    
    // 월별 출근/퇴근/지각/조퇴 내역 조회 기능
 	List<AttDTO> monthlyList(@Param("user_id") String user_id, @Param("yearMonth") String yearMonth);
    
    // 출근/퇴근/지각/조퇴 내역 조회 기능
    List<AttDTO> attList(String user_id);
    
    // 전체 직원 근태 조회 기능
    List<AttDTO> attListAll();
    
    // 특정 출퇴근 기록 조회
    AttDTO attDetail(int att_idx);

    // 출/퇴근 시간 설정 기능 -- 출퇴근 기준 시간 생성
	int createAttSetting(AttSettingDTO dto);

    // 출/퇴근 시간 설정 기능 -- 출퇴근 기준 시간 수정
	int updateAttSetting(AttSettingDTO dto);

    // 출/퇴근 시간 설정 기능 -- 현재 적용되는 기준 시간 조회 
	AttSettingDTO getAttSetting();

	// 근태 통계
	List<Map<String, Object>> attStat(String user_id);

	// 전체 직원 근태 통계
	List<Map<String, Object>> attStatAll();

    // 최근 30일 출근/퇴근/지각/조퇴 내역 조회 기능
    List<AttDTO> recent30days(String user_id);
	
	
}
