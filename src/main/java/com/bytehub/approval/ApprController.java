package com.bytehub.approval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytehub.member.FileDTO;
import com.bytehub.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@RestController
@RequiredArgsConstructor
public class ApprController {

	private final ApprService service;

	/**
	 * 결재 문서 생성 API
	 * - 결재 문서 정보와 첨부 파일을 함께 저장
	 * - 결재 라인과 결재 이력 자동 생성
	 * - 파일은 임시 디렉토리에 저장
	 */
	@PostMapping("/appr/create")
	public Map<String, Object> createApproval(@RequestParam("writer_id") String writerId,
			@RequestParam("subject") String subject, @RequestParam("content") String content,
			@RequestParam("appr_type") String apprType,
			@RequestParam(value = "vac_start", required = false) String vacStart,
			@RequestParam(value = "vac_end", required = false) String vacEnd,
			@RequestParam(value = "files", required = false) List<MultipartFile> files) {
		Map<String, Object> result = new HashMap<>();
		try {
			ApprDTO appr = new ApprDTO();
			appr.setWriter_id(writerId);
			appr.setSubject(subject);
			appr.setAppr_type(apprType);
			appr.setContent(content);
			appr.setAppr_date(LocalDateTime.now());
			
			// 연차일 경우 시작일과 종료일 저장
			if ("연차".equals(apprType) && vacStart != null && vacEnd != null) {
				appr.setVac_start(Date.valueOf(vacStart));
				appr.setVac_end(Date.valueOf(vacEnd));
			}

			// 파일 저장: FileDTO 리스트 생성
			List<FileDTO> fileDTOList = new ArrayList<>();
			String uploadDir = System.getProperty("java.io.tmpdir") + "/upload";
			File dir = new File(uploadDir);
			if (!dir.exists()) {
				boolean created = dir.mkdirs();
				log.info("업로드 디렉토리 생성: {} - {}", uploadDir, created);
			}
			
			if (files != null) {
				log.info("업로드할 파일 개수: {}", files.size());
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						try {
							String oriFilename = file.getOriginalFilename();
							String newFilename = System.currentTimeMillis() + "_" + oriFilename;
							String uploadPath = uploadDir + "/" + newFilename;
							
							log.info("파일 업로드 시작: {} -> {}", oriFilename, uploadPath);
							
							File uploadFile = new File(uploadPath);
							file.transferTo(uploadFile);
							
							// 파일이 실제로 생성되었는지 확인
							if (uploadFile.exists()) {
								log.info("파일 업로드 성공: {} (크기: {} bytes)", uploadPath, uploadFile.length());
							} else {
								                log.info("파일 업로드 실패: 파일이 생성되지 않음 - {}", uploadPath);
							}

							FileDTO fileDTO = new FileDTO();
							fileDTO.setOri_filename(oriFilename);
							fileDTO.setNew_filename(newFilename);
							fileDTO.setFile_type("approval");
							// appr_idx는 service에서 set
							fileDTOList.add(fileDTO);
						} catch (IOException e) {
							            log.info("파일 업로드 실패: {} - {}", file.getOriginalFilename(), e.getMessage(), e);
							// 파일 업로드 실패 시에도 계속 진행
						}
					} else {
						log.warn("빈 파일 발견: {}", file.getOriginalFilename());
					}
				}
			} else {
				log.info("업로드할 파일이 없습니다.");
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

	/**
	 * 결재 상태 변경 API
	 * - 결재 승인 또는 반려 처리
	 * - 결재 이력에 처리 시간과 사유 기록
	 */
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

	/**
	 * 내가 작성한 결재 문서 조회 API
	 * - 특정 사용자가 작성한 모든 결재 문서 목록 조회
	 * - 결재 상태별로 필터링 가능
	 */
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

	/**
	 * 내가 결재한 문서 이력 조회 API
	 * - 특정 사용자가 결재 처리한 모든 문서 목록 조회
	 * - 결재 처리 시간과 결과 포함
	 */
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

	/**
	 * 내가 결재할 문서 목록 조회 API
	 * - 현재 사용자가 결재 처리해야 할 문서 목록 조회
	 * - 대기중인 결재 문서만 표시
	 */
	@GetMapping("/appr/toapprove")
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

	/**
	 * 전체 결재 문서 목록 조회 API
	 * - 시스템의 모든 결재 문서 목록 조회
	 * - 관리자 권한이 필요한 기능
	 */
	@GetMapping("/appr/all")
	public Map<String, Object> getAllApprovals(@RequestParam(value = "user_id", required = false) String user_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			result.put("success", true);
			result.put("data", service.getAllApprovals(user_id));
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "조회 실패: " + e.getMessage());
		}
		return result;
	}

	/**
	 * 결재 문서 상세 조회 API
	 * - 특정 결재 문서의 상세 정보 조회
	 * - 첨부 파일 목록과 결재 이력 포함
	 */
	@GetMapping("/appr/detail/{appr_idx}")
	public Map<String, Object> getApprovalDetail(@PathVariable int appr_idx, @RequestParam(value = "user_id", required = false) String user_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> detail = service.getApprovalDetail(appr_idx, user_id);
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

	/**
	 * 결재 문서의 결재 이력 조회 API
	 * - 특정 결재 문서의 모든 결재 처리 이력 조회
	 * - 결재자 정보와 처리 시간 포함
	 */
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

	/**
	 * 첨부 파일 다운로드 API
	 * - 결재 문서에 첨부된 파일 다운로드
	 * - 원본 파일명으로 다운로드 제공
	 */
	@GetMapping("/appr/download/{file_idx}")
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable int file_idx) {
		try {
			// 파일 정보 조회
			FileDTO fileInfo = service.getFileById(file_idx);
			if (fileInfo == null) {
				return ResponseEntity.notFound().build();
			}
			
			// 파일 경로 설정
			String uploadDir = System.getProperty("java.io.tmpdir") + "/upload";
			File file = new File(uploadDir + "/" + fileInfo.getNew_filename());
			
			if (!file.exists()) {
				return ResponseEntity.notFound().build();
			}
			
			// 파일 스트림 생성
			InputStream inputStream = new FileInputStream(file);
			InputStreamResource resource = new InputStreamResource(inputStream);
			
			// HTTP 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", fileInfo.getOri_filename());
			
			return ResponseEntity.ok()
					.headers(headers)
					.body(resource);
					
		} catch (IOException e) {
			            log.info("파일 다운로드 실패: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().build();
		}
	}

	// 연차/월차 자동 생성 API (전체 또는 선택 사원)
	@PostMapping("/leave/generate")
	public Map<String, Object> generateLeave(@RequestBody Map<String, Object> request,
			@RequestHeader("Authorization") String token) {

		Map<String, Object> result = new HashMap<>();

		try {
			// JWT 토큰 검증
			Map<String, Object> tokenData = JwtUtils.readToken(token);
			String userId = (String) tokenData.get("id");
			if (userId == null) {
				result.put("success", false);
				result.put("msg", "유효하지 않은 토큰입니다.");
				return result;
			}

			// 선택된 사원 목록 확인
			List<String> selectedMembers = (List<String>) request.get("selectedMembers");
			
			if (selectedMembers == null || selectedMembers.isEmpty()) {
				// 전체 사원 대상 연차 생성 (기존 방식)
				service.generateLeave();
				result.put("success", true);
				result.put("msg", "전체 사원 연차/월차 생성 완료");
			} else {
				// 선택된 사원들만 대상 연차 생성 (새로운 방식)
				service.generateLeaveForSelected(selectedMembers);
				result.put("success", true);
				result.put("msg", selectedMembers.size() + "명의 사원 연차/월차 생성 완료");
			}

		} catch (Exception e) {
			log.error("연차 생성 실패: ", e);
			result.put("success", false);
			result.put("msg", "연차 생성 중 오류가 발생했습니다.");
			result.put("error", e.getMessage());
		}

		return result;
	}

	// 개인 잔여 연차 조회 API
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

	// 연차 상세 내역 조회 (GET)
	@GetMapping("/leave/detail")
	public Map<String, Object> leaveDetail(@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<>();

		Map<String, Object> tokenData = JwtUtils.readToken(token);
		String userId = (String) tokenData.get("id");
		if (userId == null) {
			result.put("success", false);
			result.put("msg", "유효하지 않은 토큰입니다.");
			return result;
		}

		List<ApprDTO> leaveList = service.leaveDetail(userId);
		
		result.put("success", true);
		result.put("data", leaveList);
		
		return result;
	}



	// 전체 사원 연차 현황 조회 API
	@GetMapping("/leave/all")
	public Map<String, Object> getAllMembersLeave(@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> tokenData = JwtUtils.readToken(token);
			String userId = (String) tokenData.get("id");
			if (userId == null) {
				result.put("success", false);
				result.put("msg", "유효하지 않은 토큰입니다.");
				return result;
			}
			
			// 관리자 권한 체크 (필요 시)
			// MemberDTO user = memberDAO.getMemberById(userId);
			// if (user.getLv_idx() > 3) {
			//     result.put("success", false);
			//     result.put("msg", "권한이 없습니다.");
			//     return result;
			// }
			
			List<Map<String, Object>> allMembersLeave = service.getAllMembersLeave();
			result.put("success", true);
			result.put("data", allMembersLeave);
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "전체 사원 연차 조회 중 오류가 발생했습니다.");
			result.put("error", e.getMessage());
		}
		return result;
	}

	// 연차 수정 API
	@PutMapping("/leave/update")
	public Map<String, Object> updateLeave(@RequestBody Map<String, Object> request,
			@RequestHeader("Authorization") String token) {
		
		Map<String, Object> result = new HashMap<>();
		
		try {
			// 기본 토큰 검증만
			Map<String, Object> tokenData = JwtUtils.readToken(token);
			String userId = (String) tokenData.get("id");
			if (userId == null) {
				result.put("success", false);
				result.put("msg", "유효하지 않은 토큰입니다.");
				return result;
			}
			
			String targetUserId = (String) request.get("targetUserId");
			Float newRemainDays = Float.parseFloat(request.get("remainDays").toString());
			
			service.updateLeave(targetUserId, newRemainDays);
			
			result.put("success", true);
			result.put("msg", "연차가 수정되었습니다.");
			
		} catch (Exception e) {
			log.error("연차 수정 실패: ", e);
			result.put("success", false);
			result.put("msg", "연차 수정 실패: " + e.getMessage());
		}
		
		return result;
	}

	// 현재 연차 정책 조회 API
	@GetMapping("/leave/setting/current")
	public Map<String, Object> getCurrentLeaveSetting(@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> tokenData = JwtUtils.readToken(token);
			String userId = (String) tokenData.get("id");
			if (userId == null) {
				result.put("success", false);
				result.put("msg", "유효하지 않은 토큰입니다.");
				return result;
			}
			
			LeaveSettingDTO setting = service.getCurrentLeaveSetting();
			result.put("success", true);
			result.put("data", setting);
			
		} catch (Exception e) {
			log.error("연차 정책 조회 실패: ", e);
			result.put("success", false);
			result.put("msg", "연차 정책 조회 실패: " + e.getMessage());
		}
		return result;
	}
	
	// 모든 연차 정책 조회 API
	@GetMapping("/leave/setting/all")
	public Map<String, Object> getAllLeaveSettings(@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> tokenData = JwtUtils.readToken(token);
			String userId = (String) tokenData.get("id");
			if (userId == null) {
				result.put("success", false);
				result.put("msg", "유효하지 않은 토큰입니다.");
				return result;
			}
			
			List<LeaveSettingDTO> settings = service.getAllLeaveSettings();
			result.put("success", true);
			result.put("data", settings);
			
		} catch (Exception e) {
			log.error("연차 정책 목록 조회 실패: ", e);
			result.put("success", false);
			result.put("msg", "연차 정책 목록 조회 실패: " + e.getMessage());
		}
		return result;
	}
	
	// 연차 정책 등록/수정 API
	@PostMapping("/leave/setting")
	public Map<String, Object> saveLeaveSetting(@RequestBody LeaveSettingDTO setting,
			@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> tokenData = JwtUtils.readToken(token);
			String userId = (String) tokenData.get("id");
			if (userId == null) {
				result.put("success", false);
				result.put("msg", "유효하지 않은 토큰입니다.");
				return result;
			}
			
			service.saveLeaveSetting(setting);
			result.put("success", true);
			result.put("msg", "연차 정책이 저장되었습니다.");
			
		} catch (Exception e) {
			log.error("연차 정책 저장 실패: ", e);
			result.put("success", false);
			result.put("msg", "연차 정책 저장 실패: " + e.getMessage());
		}
		return result;
	}

	// 연차 사용 시 DB 차감 기능 -- 결재에 있음

	// 연차 승인 시 일정 기록 기능


}
