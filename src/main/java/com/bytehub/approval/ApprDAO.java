package com.bytehub.approval;

import org.apache.ibatis.annotations.Mapper;
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
	
    // 연차 상세 내역 조회
    List<ApprDTO> leaveDetail(String loginId);

    // 기안자의 lv_idx와 dept_idx를 받아 결재자 lv_idx 1,2(고정 1명), 3(같은 부서 팀장)만 반환
    List<ApprLineDTO> getDynamicApprLine(Map<String, Object> param);
	
    int getApprHistoryIdx(int appr_his_idx);
	ApprDTO getApprIdx(int appr_idx);
	void minusLeave(String writer_id);
	
    // 전체 사원 연차 현황 조회
    List<Map<String, Object>> getAllMembersLeave();
    
}
