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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/appr")
public class ApprController {

    @Autowired
    private ApprService service;

    @PostMapping("/create")
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
    @PutMapping("/status")
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

    @GetMapping("/my")
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

    @GetMapping("/history")
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
<<<<<<< Updated upstream

    @GetMapping("/list")
    public Map<String, Object> getAllApprovals() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", service.getAllApprovals());
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 실패: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/detail/{appr_idx}")
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
=======
    
    // 총 연차 조회
    // 사용 연차 조회
    // 잔여 연차 조회
    // 연차 상세 내역 조회
    

    
>>>>>>> Stashed changes
}
