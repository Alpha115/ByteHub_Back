package com.bytehub.approval;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.File;

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
import org.springframework.web.multipart.MultipartFile;

import com.bytehub.utils.JwtUtils;
import com.bytehub.member.FileDTO;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/appr")
public class ApprController {

    @Autowired
    private ApprService service;

    // 결재 문서 생성 (파일 업로드 포함, @RequestParam 방식)
    @PostMapping("/create")
    public Map<String, Object> createApproval(
        @RequestParam("writer_id") String writerId,
        @RequestParam("subject") String subject,
        @RequestParam("content") String content,
        @RequestParam("appr_type") String apprType,
        @RequestParam(value = "vac_start", required = false) String vacStart,
        @RequestParam(value = "vac_end", required = false) String vacEnd,
        @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            ApprDTO appr = new ApprDTO();
            appr.setWriter_id(writerId);
            appr.setSubject(subject);
            appr.setAppr_type(apprType);
            appr.setContent(content);
            appr.setAppr_date(LocalDateTime.now());
            // 연차일 경우 vac_start, vac_end도 저장 (필드가 있다면)
            // appr.setVac_start(vacStart);
            // appr.setVac_end(vacEnd);

            // 파일 저장: FileDTO 리스트 생성
            List<FileDTO> fileDTOList = new ArrayList<>();
            String uploadDir = "C:/upload";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (files != null) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String oriFilename = file.getOriginalFilename();
                        String newFilename = System.currentTimeMillis() + "_" + oriFilename;
                        String uploadPath = uploadDir + "/" + newFilename;
                        file.transferTo(new File(uploadPath));

                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setOri_filename(oriFilename);
                        fileDTO.setNew_filename(newFilename);
                        fileDTO.setFile_type("approval");
                        // appr_idx는 service에서 set
                        fileDTOList.add(fileDTO);
                    }
                }
            }

            service.createApprWithLineAndFiles(appr, fileDTOList);
            result.put("success", true);
            result.put("msg", "결재 문서와 결재라인, 파일이 등록되었습니다.");
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

    @GetMapping("/toapprove")
    public Map<String, Object> getToApproveList(@RequestParam String user_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", service.getToApproveList(user_id));
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "조회 실패: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/all")
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
        Map<String, Object> detail = service.getApprovalDetail(appr_idx);
        // 파일 리스트 추가
        List<FileDTO> files = service.getFilesByApprIdx(appr_idx);
        detail.put("files", files);
        result.put("success", true);
        result.put("data", detail);
    } catch (Exception e) {
        result.put("success", false);
        result.put("msg", "조회 실패: " + e.getMessage());
    }
    return result;
}

    @GetMapping("/history/{appr_idx}")
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
    @GetMapping("/leave/my")
    public Map<String, Object> myLeave(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> tokenData = JwtUtils.readToken(token);
            String userId = (String) tokenData.get("id");
            if (userId == null) {
                result.put("success", false);
                result.put("msg", "유효하지 않은 토큰입니다.");
                return result;
            }
            Object leave = service.myLeave(userId);
            result.put("success", true);
            result.put("data", leave);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "연차/월차 조회 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    // 사용 이력 조회 (GET)

    
}


