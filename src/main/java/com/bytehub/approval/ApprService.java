package com.bytehub.approval;

import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.bytehub.member.FileDTO;
import com.bytehub.member.MemberDAO;
import com.bytehub.member.MemberDTO;
import com.bytehub.schedule.ScdService;
import com.bytehub.schedule.ScdDTO;
import com.bytehub.notification.NotiService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprService {

    private final ApprDAO dao;

    private final MemberDAO memberDAO;

    private final ScdService scdService;
    private final NotiService notiService;
	

    public int createApprWithLineAndFiles(ApprDTO appr, List<FileDTO> fileList) {
        // 1. 기안자 정보 조회
        MemberDTO writer = memberDAO.getMemberById(appr.getWriter_id());
        int writerLvIdx = writer != null ? writer.getLv_idx() : 7;
        int writerDeptIdx = writer != null ? writer.getDept_idx() : 11;
        // 기안 권한 체크
        if (writerLvIdx == 7 || writerLvIdx == 1) {
            throw new RuntimeException("기안 권한이 없습니다.");
        }
        // 2. approval 문서 생성
        dao.createAppr(appr);
        int appr_idx = appr.getAppr_idx();
        // 3. 결재 라인 생성 (사장, 이사, 같은 부서 팀장)
        Map<String, Object> param = new HashMap<>();
        param.put("writerLvIdx", writerLvIdx);
        param.put("writerDeptIdx", writerDeptIdx);
        List<ApprLineDTO> apprLineList = dao.getDynamicApprLine(param);
        for (ApprLineDTO line : apprLineList) {
            // 본인(user_id) 결재라인은 생성하지 않음
            if (line.getUser_id().equals(appr.getWriter_id())) continue;
            Map<String, Object> history = new HashMap<>();
            history.put("appr_idx", appr_idx);
            history.put("checker_id", line.getUser_id());
            history.put("step", line.getStep());
            history.put("lv_idx", line.getLv_idx());
            history.put("status", "대기중");
            history.put("reason", null);
            history.put("check_time", LocalDateTime.now());
            dao.appr_checker(history);
        }
        // 4. 파일 정보 저장
        if (fileList != null) {
            for (FileDTO fileDTO : fileList) {
                fileDTO.setAppr_idx(appr_idx);
                dao.insertFile(fileDTO);
            }
        }
        
        // 5. 결재자들에게 실시간 알림 전송
        for (ApprLineDTO line : apprLineList) {
            if (!line.getUser_id().equals(appr.getWriter_id())) {
                notiService.sendApprovalRequestNotification(
                    line.getUser_id(),
                    writer.getName(),
                    appr.getSubject(),
                    appr.getAppr_type()
                );
            }
        }
        
        return appr_idx;
    }

    public int createApprWithLine(ApprDTO appr) {
        MemberDTO writer = memberDAO.getMemberById(appr.getWriter_id());
        int writerLvIdx = writer != null ? writer.getLv_idx() : 7;
        int writerDeptIdx = writer != null ? writer.getDept_idx() : 0;
        if (writerLvIdx == 7 || writerLvIdx == 1) {
            throw new RuntimeException("기안 권한이 없습니다.");
        }
        dao.createAppr(appr);
        int appr_idx = appr.getAppr_idx();
        Map<String, Object> param = new HashMap<>();
        param.put("writerLvIdx", writerLvIdx);
        param.put("writerDeptIdx", writerDeptIdx);
        List<ApprLineDTO> apprLineList = dao.getDynamicApprLine(param);
        for (ApprLineDTO line : apprLineList) {
            // 본인(user_id) 결재라인은 생성하지 않음
            if (line.getUser_id().equals(appr.getWriter_id())) continue;
            Map<String, Object> history = new HashMap<>();
            history.put("appr_idx", appr_idx);
            history.put("checker_id", line.getUser_id());
            history.put("step", line.getStep());
            history.put("lv_idx", line.getLv_idx());
            history.put("status", "대기중");
            history.put("reason", null);
            history.put("check_time", LocalDateTime.now());
            dao.appr_checker(history);
        }
        
        // 결재자들에게 실시간 알림 전송
        for (ApprLineDTO line : apprLineList) {
            if (!line.getUser_id().equals(appr.getWriter_id())) {
                notiService.sendApprovalRequestNotification(
                    line.getUser_id(),
                    writer.getName(),
                    appr.getSubject(),
                    appr.getAppr_type()
                );
            }
        }
        
        return appr_idx;
    }
    public int updateStatus(Map<String, Object> param) {

    int result = dao.updateStatus(param);
    
    // 결재 상태 변경 시 기안자에게 실시간 알림 전송
    if (result > 0) {
        try {
            // 결재자 정보 조회
            String checkerId = (String) param.get("checker_id");
            MemberDTO checker = memberDAO.getMemberById(checkerId);
            String checkerName = checker != null ? checker.getName() : "결재자";
            
            // 결재 문서 정보 조회
            int appr_his_idx = (int) param.get("appr_his_idx");
            int appr_idx = dao.getApprHistoryIdx(appr_his_idx);
            ApprDTO appr = dao.getApprIdx(appr_idx);
            
            if (appr != null) {
                String status = (String) param.get("status");
                notiService.sendApprovalStatusNotification(
                    appr.getWriter_id(),
                    checkerName,
                    appr.getSubject(),
                    status
                );
            }
        } catch (Exception e) {
            log.error("결재 상태 알림 전송 실패: {}", e.getMessage());
        }
    }




    // 결재가 승인될 때만 연차 차감
    String status = (String) param.get("status");
    if ("승인완료".equals(status)) {
        // appr_his_idx로 appr_idx 조회 → appr_idx로 ApprDTO 조회 → appr_type, writer_id 확인
        int appr_his_idx = (int) param.get("appr_his_idx");
        int appr_idx = dao.getApprHistoryIdx(appr_his_idx);
        ApprDTO appr = dao.getApprIdx(appr_idx);

        if ("연차".equals(appr.getAppr_type())) {
            // 연차 날짜 기반으로 차감일수 계산
            double daysToDeduct = calculateDaysToDeduct(appr);
            int updateResult = dao.minusLeaveByDays(appr.getWriter_id(), daysToDeduct);
            
            // 연차 차감 실패 시 로그
            if (updateResult == 0) {
                log.info("연차 차감 실패: 사용자 {}, 차감일수 {}", appr.getWriter_id(), daysToDeduct);
            } else {
                // 연차 차감 성공 시 일정 생성
                try {
                    createScheduleForLeave(appr);
                    log.info("연차 일정 생성 완료: 사용자 {}, 기간 {} ~ {}", 
                             appr.getWriter_id(), appr.getVac_start(), appr.getVac_end());
                } catch (Exception e) {
                    log.error("연차 일정 생성 실패: 사용자 {}, 오류: {}", appr.getWriter_id(), e.getMessage());
                }
            }
        }
    }
    return result;
       
    }
    
    // 연차 사용량 계산 (날짜 기반)
    private double calculateDaysToDeduct(ApprDTO appr) {
        if (appr.getVac_start() != null && appr.getVac_end() != null) {
            // 시작일과 종료일 사이의 일수 계산 (종료일 포함)
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                appr.getVac_start().toLocalDate(), appr.getVac_end().toLocalDate()) + 1;
            return (double) days;
        }
        return 1.0; // 기본값
    }

    // 연차 승인 시 일정 생성
    private void createScheduleForLeave(ApprDTO appr) {
        ScdDTO schedule = new ScdDTO();
        schedule.setUser_id(appr.getWriter_id());
        schedule.setScd_type("연차");
        schedule.setType_idx(appr.getAppr_idx());
        schedule.setSubject(appr.getSubject() + " (연차)");
        schedule.setStart_date(appr.getVac_start());
        schedule.setEnd_date(appr.getVac_end());
        
        scdService.insert(schedule);
    }

    public List<Map<String, Object>> getMyAppr(String writer_id) {
        return dao.getMyAppr(writer_id);
    }

    public List<Map<String, Object>> getMyHistory(String checker_id) {
        return dao.getMyHistory(checker_id);
    }

    public List<Map<String, Object>> getToApproveList(String user_id) {
        // 결재자 정보 조회
        MemberDTO checker = memberDAO.getMemberById(user_id);
        int checkerLvIdx = checker != null ? checker.getLv_idx() : 7;
        int checkerDeptIdx = checker != null ? checker.getDept_idx() : 11;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", user_id);
        param.put("checker_lv_idx", checkerLvIdx);
        param.put("checker_dept_idx", checkerDeptIdx);
        return dao.getToApproveList(param);
    }

    public List<Map<String, Object>> getAllApprovals(String user_id) {
        // 사용자 정보 조회
        MemberDTO user = memberDAO.getMemberById(user_id);
        int userLvIdx = user != null ? user.getLv_idx() : 7;
        int userDeptIdx = user != null ? user.getDept_idx() : 11;
        Map<String, Object> param = new HashMap<>();
        param.put("checker_lv_idx", userLvIdx);
        param.put("checker_dept_idx", userDeptIdx);
        return dao.getAllApprovals(param);
    }

    public Map<String, Object> getApprovalDetail(int appr_idx, String user_id) {
        Map<String, Object> param = new HashMap<>();
        param.put("appr_idx", appr_idx);
        
        // user_id가 제공된 경우에만 권한 체크
        if (user_id != null && !user_id.isEmpty()) {
            MemberDTO user = memberDAO.getMemberById(user_id);
            int userLvIdx = user != null ? user.getLv_idx() : 7;
            int userDeptIdx = user != null ? user.getDept_idx() : 11;
            param.put("checker_lv_idx", userLvIdx);
            param.put("checker_dept_idx", userDeptIdx);
        }
        
        return dao.getApprovalDetail(param);
    }

    public List<Map<String, Object>> getApprovalHistory(int appr_idx) {
        return dao.getApprovalHistory(appr_idx);
    }
    
    public List<FileDTO> getFilesByApprIdx(int appr_idx) {
        return dao.getFilesByApprIdx(appr_idx);
    }
    
    public FileDTO getFileById(int file_idx) {
        return dao.getFileById(file_idx);
    }
    
    // -------------------------------------------------------- 연차 --------------------------------------------------------
    
    // 연/월차 부여 (설정 기반)
	public void generateLeave() {
		// 현재 연차 정책 조회
		LeaveSettingDTO setting = getCurrentLeaveSetting();
		
		/* 신규 입사자 (입사 1년 미만) 대상 월차 생성 
	       설정값에 따라 1달에 N개씩 발생 (입사월 ~ 12월까지)*/
		dao.monthlyLeaveWithSetting(setting);

        /* 입사 1년 이상자 대상 연차 생성
           설정값에 따라 기본 N개 + 근속년수당 N개씩 추가 */
		dao.annualLeaveWithSetting(setting);
		
	}
	
    // 개인 잔여 연차 조회
    public List<LeaveHistoryDTO> myLeave(String loginId) {
        return dao.myLeave(loginId);
    }

    // 연차 상세 내역 조회
	public List<ApprDTO> leaveDetail(String loginId) {
		return dao.leaveDetail(loginId);
	}
	
	public List<Map<String, Object>> getAllMembersLeave() {
		return dao.getAllMembersLeave();
	}
	
	// 선택된 사원들에게 정책 기반 연차 생성
	public void generateLeaveForSelected(List<String> selectedMembers) {
		// 현재 연차 정책 조회
		LeaveSettingDTO setting = getCurrentLeaveSetting();
		
		// 신규 입사자(1년 미만) 월차 생성
		dao.monthlyLeaveForSelectedWithSetting(selectedMembers, setting);
		
		// 기존 사원(1년 이상) 연차 생성  
		dao.annualLeaveForSelectedWithSetting(selectedMembers, setting);
	}
	
	// 연차 수정
	public void updateLeave(String targetUserId, Float newRemainDays) {
		dao.updateLeaveRemainDays(targetUserId, newRemainDays);
	}
	
	// 연차 정책 관리
	public LeaveSettingDTO getCurrentLeaveSetting() {
		int currentYear = java.time.LocalDateTime.now().getYear();
		LeaveSettingDTO setting = dao.getCurrentLeaveSetting(currentYear);
		
		// 현재 년도 정책이 없으면 기본값 반환
		if (setting == null) {
			setting = new LeaveSettingDTO();
			setting.setYear(currentYear);
			setting.setNewEmpBase(1);
			setting.setExistingEmpBase(15);
			setting.setAnnualIncrement(1);
			setting.setMaxAnnual(25);
		}
		return setting;
	}
	
	public List<LeaveSettingDTO> getAllLeaveSettings() {
		return dao.getAllLeaveSettings();
	}
	
	public void saveLeaveSetting(LeaveSettingDTO setting) {
		LeaveSettingDTO existing = dao.getCurrentLeaveSetting(setting.getYear());
		if (existing != null) {
			// 기존 정책이 있으면 수정
			setting.setLeaveSetIdx(existing.getLeaveSetIdx());
			dao.updateLeaveSetting(setting);
		} else {
			// 새로운 정책 생성
			dao.insertLeaveSetting(setting);
		}
	}

	public ArrayList<Map<String, Object>> leaveTeam(int idx) {
		return dao.leaveTeam(idx);
	}
    
}
