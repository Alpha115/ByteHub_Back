package com.bytehub.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApprService {

    @Autowired
    private ApprDAO dao;

    public int createApprWithLine(ApprDTO appr) {
        // 1. approval 문서 생성
        dao.createAppr(appr);
        int appr_idx = appr.getAppr_idx();

        // 2. appr_line에서 결재라인(순서, 직급, user_id) 전체 조회
        List<ApprLineDTO> apprLineList = dao.getApprLine();
        
        // 3. appr_history row 생성
        for (ApprLineDTO line : apprLineList) {
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
        return appr_idx;
    }
    public int updateStatus(Map<String, Object> param) {
        return dao.updateStatus(param);
    }

    public List<Map<String, Object>> getMyAppr(String writer_id) {
        return dao.getMyAppr(writer_id);
    }

    public List<Map<String, Object>> getMyHistory(String checker_id) {
        return dao.getMyHistory(checker_id);
    }

    public List<Map<String, Object>> getAllApprovals() {
        return dao.getAllApprovals();
    }

    public Map<String, Object> getApprovalDetail(int appr_idx) {
        return dao.getApprovalDetail(appr_idx);
    }

    public List<Map<String, Object>> getApprovalHistory(int appr_idx) {
        return dao.getApprovalHistory(appr_idx);
    }
    
    // 연/월차 생성
	public void generateLeave() {

		/* 신규 입사자 (입사 1년 미만) 대상 월차 생성 
	       1달에 1개씩, 최대 12개까지 발생 (입사월 ~ 12월까지)*/
		dao.monthlyLeave();

        /* 입사 1년 이상자 대상 연차 생성
           입사 다음 해 1월 1일부터는 기본 15개 부여
           이후 입사일 기준 1년마다 +1개씩 추가 */
		dao.annualLeave();
		
	}
	
    // 개인 잔여 연차 조회
    public List<LeaveHistoryDTO> myLeave(String loginId) {
        return dao.myLeave(loginId);
    }
}
