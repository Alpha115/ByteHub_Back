package com.bytehub.approval;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.bytehub.member.FileDTO;

@Mapper
public interface ApprDAO {
	
    int createAppr(ApprDTO appr);
    int appr_checker(Map<String, Object> param);
    List<ApprLineDTO> getApprLine();
    int updateStatus(Map<String, Object> param);
    List<Map<String, Object>> getMyAppr(String writer_id);
    List<Map<String, Object>> getMyHistory(String checker_id);
    List<Map<String, Object>> getToApproveList(Map<String, Object> param);
    List<Map<String, Object>> getAllApprovals(Map<String, Object> param);
    Map<String, Object> getApprovalDetail(Map<String, Object> param);
    List<Map<String, Object>> getApprovalHistory(int appr_idx);
    List<FileDTO> getFilesByApprIdx(int appr_idx);
    FileDTO getFileById(int file_idx);
    
    // 파일 정보 저장
    int insertFile(FileDTO fileDTO);

	// 신규 입사자 (입사 1년 미만) 대상 월차 생성 
	void monthlyLeave();
	
	// 입사 1년 이상자 대상 연차 생성
	void annualLeave();
	
    // 개인 잔여 연차 조회
    List<LeaveHistoryDTO> myLeave(String writer_id);
	


    // 기안자의 lv_idx와 dept_idx를 받아 결재자 lv_idx 1,2(고정 1명), 3(같은 부서 팀장)만 반환
    List<ApprLineDTO> getDynamicApprLine(Map<String, Object> param);
	
    // 결재히스토리리 내역 조회
    int getApprHistoryIdx(int appr_his_idx);

    // 결재 내역 조회
	ApprDTO getApprIdx(int appr_idx);


	// 날짜 기반 연차 차감
	int minusLeaveByDays(@Param("writer_id") String writer_id, @Param("days") double days);
	
    // 전체 사원 연차 현황 조회
    List<Map<String, Object>> getAllMembersLeave();
    
    // 선택된 사원들 월차 생성 (신규 입사자)
    void monthlyLeaveForSelected(List<String> selectedMembers);
    
    // 선택된 사원들 연차 생성 (기존 사원)
    void annualLeaveForSelected(List<String> selectedMembers);
    
    // 연차 잔여일 수정
    int updateLeaveRemainDays(@Param("targetUserId") String targetUserId, 
                             @Param("newRemainDays") Float newRemainDays);
    
    // 연차 정책 관리
    LeaveSettingDTO getCurrentLeaveSetting(int year);
    List<LeaveSettingDTO> getAllLeaveSettings();
    int insertLeaveSetting(LeaveSettingDTO setting);
    int updateLeaveSetting(LeaveSettingDTO setting);
    
    // 설정 기반 연차 생성
    void monthlyLeaveWithSetting(LeaveSettingDTO setting);
    void annualLeaveWithSetting(LeaveSettingDTO setting);
    
    // 선택된 사원들 설정 기반 연차 생성
    void monthlyLeaveForSelectedWithSetting(@Param("selectedMembers") List<String> selectedMembers, 
                                           @Param("setting") LeaveSettingDTO setting);
    void annualLeaveForSelectedWithSetting(@Param("selectedMembers") List<String> selectedMembers, 
                                          @Param("setting") LeaveSettingDTO setting);
	ArrayList<Map<String, Object>> leaveTeam(int idx);
	
	// 연차 삭제 (remain_days를 0으로)
	int deleteLeaveRemain(List<String> selectedMembers);
	
	// 연차 상세보기 조회
	List<Map<String, Object>> getLeaveDetail(String writer_id);
    
}
