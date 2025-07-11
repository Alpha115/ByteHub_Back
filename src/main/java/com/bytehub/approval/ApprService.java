package com.bytehub.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.bytehub.member.FileDTO;
import com.bytehub.member.MemberDAO;
import com.bytehub.member.MemberDTO;

@Service
public class ApprService {

    @Autowired
    private ApprDAO dao;

    @Autowired
    private MemberDAO memberDAO;
	

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

    public List<Map<String, Object>> getAllApprovals() {
        return dao.getAllApprovals();
    }

    public Map<String, Object> getApprovalDetail(int appr_idx) {
        return dao.getApprovalDetail(appr_idx);
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

    // 연차 상세 내역 조회
	public List<ApprDTO> leaveDetail(String loginId) {
		return dao.leaveDetail(loginId);
	}
    
    
    
}
