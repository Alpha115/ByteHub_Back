package com.bytehub.approval;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@RestController
// @RequestMapping("/appr")
public class ApprController {

    @Autowired
    private ApprService service;

    @PostMapping("/appr/create")
    public Map<String, Object> createApproval(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            ApprDTO appr = new ApprDTO();
            appr.setWriter_id((String) param.get("writer_id"));
            appr.setSubject((String) param.get("subject"));
            appr.setAppr_type((String) param.get("appr_type"));
            appr.setContent((String) param.get("content"));
            appr.setAppr_date(LocalDateTime.now());

            service.createApprWithLine(appr);
            result.put("success", true);
            result.put("msg", "결재 문서와 결재라인 내역이 등록되었습니다.");
            result.put("appr_idx", appr.getAppr_idx());
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "등록 실패: " + e.getMessage());
        }
        return result;
    }
    @PutMapping("/appr/status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            int appr_his_idx = (int) param.get("appr_his_idx");
            String status = (String) param.get("status");
            String reason = (String) param.get("reason");
            LocalDateTime check_time = LocalDateTime.now();

            Map<String, Object> updateParam = new HashMap<>();
            updateParam.put("appr_his_idx", appr_his_idx);
            updateParam.put("status", status);
            updateParam.put("reason", reason);
            updateParam.put("check_time", check_time);

            service.updateStatus(updateParam);

            result.put("success", true);
            result.put("msg", "결재 상태가 변경되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "상태 변경 실패: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/appr/my")
    public Map<String, Object> getMyAppr(@RequestParam String writer_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", service.getMyAppr(writer_id));
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 실패: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/appr/history")
    public Map<String, Object> getMyHistory(@RequestParam String checker_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", service.getMyHistory(checker_id));
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 실패: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/appr/detail/{appr_idx}")
    public Map<String, Object> getApprovalDetail(@PathVariable int appr_idx) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", service.getApprovalDetail(appr_idx));
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 실패: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/appr/history/{appr_idx}")
    public Map<String, Object> getApprovalHistory(@PathVariable int appr_idx) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", service.getApprovalHistory(appr_idx));
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 실패: " + e.getMessage());
        }
        return result;
    }
    
    // 연/월차 생성
    @PostMapping("/leave/generate")
   public Map<String, Object> generateLeave(
		   @RequestBody LeaveHistoryDTO dto,
		   @RequestHeader Map<String, String> header) {
    	
    	Map<String, Object> result = new HashMap<>(); // 응답 데이터 저~장~
    	
    	String loginId = null;
        boolean success = false;
        
        // JWT 토큰에서 로그인 ID 추출 (try-catch로 JWT 오류 처리)
	    try {
	    	String token = header.get("authorization");
	    	log.info("수정 API - 받은 토큰: {}", token);
	    	Map<String, Object> tokenData = JwtUtils.readToken(token);
	    	log.info("수정 API - 토큰 파싱 결과: {}", tokenData);
	    	loginId = (String) tokenData.get("id");
	    	log.info("수정 API - 추출된 loginId: {}", loginId);
	    } catch (Exception e) {
	    	log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
	    	// JWT 토큰 파싱 실패 시 프론트엔드에서 전송한 사용자 ID 사용
	    	loginId = dto.getWriter_id();
	    	log.info("JWT 파싱 실패로 인해 프론트엔드에서 전송한 사용자 ID 사용: {}", loginId);
	    }
	    
	    try {
	        service.generateLeave();
	        success = true;
	    } catch (Exception e) {
	        log.error("연차 생성 실패", e);
	        success = false;
	    }
    	
    	result.put("success", success); // 성공 여부
        result.put("msg", success ? "연차/월차 생성 완료" : "연차 생성 실패");

       return result;
    	
    }
    
    // 개인 잔여 연차 조회 (GET)
    // 사용 이력 조회 (GET)

    
}


