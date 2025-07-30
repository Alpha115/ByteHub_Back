package com.bytehub.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytehub.notification.NotiService;
import com.bytehub.member.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CloudController {

    private final CloudService service;
    private final NotiService notiService;

    // 파일 크기 제한 (100MB)
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;
    
    // 파일 저장 경로 (크로스 플랫폼 지원)
    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/upload/cloud";

    /**
     * 파일 업로드 API
     * - multipart/form-data 형식으로 파일을 업로드 받음
     * - 파일 크기 제한: 100MB
     * - 업로드 시 user_id, dept_idx, 파일명, 시간 정보 저장
     */
    @PostMapping("/cloud/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("expireDate") String expireDate,
            @RequestParam("deptIdx") int deptIdx,
            @RequestParam("user_id") String user_id) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 빈 파일 체크
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "업로드할 파일이 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 파일 크기 제한 체크
        if (file.getSize() > MAX_FILE_SIZE) {
            response.put("success", false);
            response.put("message", "파일 크기가 100MB를 초과합니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 업로드 디렉토리 생성
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                log.info("업로드 디렉토리 생성: {} - {}", UPLOAD_DIR, created);
            }
            
            // 파일 저장
            String oriFilename = file.getOriginalFilename();
            String newFilename = System.currentTimeMillis() + "_" + oriFilename;
            String uploadPath = UPLOAD_DIR + "/" + newFilename;
            
            log.info("파일 업로드 시작: {} -> {}", oriFilename, uploadPath);
            
            File uploadFile = new File(uploadPath);
            file.transferTo(uploadFile);
            
            // 파일이 실제로 생성되었는지 확인
            if (uploadFile.exists()) {
                log.info("파일 업로드 성공: {} (크기: {} bytes)", uploadPath, uploadFile.length());
            } else {
                log.error("파일 업로드 실패: 파일이 생성되지 않음 - {}", uploadPath);
                throw new RuntimeException("파일 저장 실패");
            }
            
            // 파일 정보 설정
            CloudDTO cloudDTO = new CloudDTO();
            cloudDTO.setFilename(newFilename); // 새로운 파일명 저장
            cloudDTO.setDept_idx(deptIdx);
            cloudDTO.setUserId(user_id);
            
            // 서비스에 파일 저장 요청
            CloudDTO savedFile = service.saveFile(cloudDTO);

            // 파일 업로드 성공 시 같은 부서 멤버들에게 실시간 알림 전송
            try {
                // 사용자 정보 조회하여 부서명 가져오기
                MemberDTO userInfo = service.getUserDeptInfo(user_id);
                String deptName = userInfo != null && userInfo.getDept_name() != null ? 
                    userInfo.getDept_name() : "팀";
                
                notiService.sendFileUploadNotification(
                    user_id,
                    oriFilename, // 원본 파일명 사용
                    deptName,
                    deptIdx
                );
            } catch (Exception e) {
                log.error("파일 업로드 알림 전송 실패: {}", e.getMessage());
            }

            response.put("success", true);
            response.put("data", savedFile);
            response.put("message", "파일 업로드 성공");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "파일 업로드 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 파일 다운로드 API
     */
    @GetMapping("/cloud/download/{file_idx}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable int file_idx,
            @RequestParam("user_id") String user_id) {
        try {
            // 파일 정보 조회
            Map<String, Object> fileInfo = service.getFileInfo(file_idx);
            if (fileInfo == null) {
                log.info("파일 정보를 찾을 수 없음: file_idx = {}", file_idx);
                return ResponseEntity.notFound().build();
            }
            
            String filename = (String) fileInfo.get("filename");
            String filePath = UPLOAD_DIR + "/" + filename;
            File file = new File(filePath);
            
            log.info("다운로드 요청 - file_idx: {}, filename: {}, filePath: {}, exists: {}, size: {} bytes", 
                    file_idx, filename, filePath, file.exists(), file.exists() ? file.length() : 0);
            
            if (!file.exists()) {
                log.info("파일이 존재하지 않음: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 다운로드 로그 저장
            service.saveDownLog(file_idx, user_id);
            
            // 파일 스트림 생성
            InputStream inputStream = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            // HTTP 헤더 설정 (한글 파일명 인코딩 처리)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            // 한글 파일명을 URL 인코딩
            String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment", encodedFilename);
            
            log.info("파일 다운로드 성공: {} (크기: {} bytes)", filePath, file.length());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (IOException e) {
            log.info("파일 다운로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 파일 삭제 API
     */
    @DeleteMapping("/cloud/delete/{file_idx}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable int file_idx,
            @RequestParam("user_id") String user_id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // DB에서 파일 정보 삭제 (삭제 로그 포함)
            boolean deleted = service.deleteFile(file_idx, user_id);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "파일 삭제 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "삭제할 파일을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "파일 삭제 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

     /**
     * 파일 리스트 보기기
     */
    @GetMapping("/cloud/list")
    public ResponseEntity<Map<String, Object>> getFileList(
        @RequestParam("deptIdx") int deptIdx) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> files = service.getFileList(deptIdx);
            
            // 각 파일에 실제 파일 크기 추가
            for (Map<String, Object> file : files) {
                String filename = (String) file.get("filename");
                if (filename != null) {
                    File physicalFile = new File(UPLOAD_DIR + "/" + filename);
                    if (physicalFile.exists()) {
                        file.put("file_size", physicalFile.length());
                    } else {
                        file.put("file_size", 0L);
                    }
                }
            }
            
            response.put("success", true);
            response.put("data", files);
            response.put("message", "파일 목록 조회 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.info("파일 목록 조회 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "파일 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 모든 부서 목록 조회
     */
    @GetMapping("/cloud/departments")
    public ResponseEntity<Map<String, Object>> getAllDepartments() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> departments = service.getAllDepartments();
            
            response.put("success", true);
            response.put("data", departments);
            response.put("message", "부서 목록 조회 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.info("부서 목록 조회 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "부서 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 링크 저장 API
     */
    @PostMapping("/cloud/link/save")
    public ResponseEntity<Map<String, Object>> saveLink(
            @RequestParam("linkName") String linkName,
            @RequestParam("url") String url,
            @RequestParam("user_id") String user_id) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 빈 값 체크
        if (linkName == null || linkName.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "링크 이름을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (url == null || url.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "URL을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // URL 형식 검증 (간단한 검증)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            response.put("success", false);
            response.put("message", "올바른 URL 형식이 아닙니다. (http:// 또는 https://로 시작해야 합니다)");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 링크 정보 설정
            LinkDTO linkDTO = new LinkDTO();
            linkDTO.setLink_name(linkName.trim());
            linkDTO.setUrl(url.trim());
            linkDTO.setUser_id(user_id);
            
            // 서비스에 링크 저장 요청
            LinkDTO savedLink = service.saveLink(linkDTO);

            // 링크 저장 성공 시 같은 부서 멤버들에게 실시간 알림 전송
            try {
                // 사용자 정보 조회하여 부서 정보 가져오기
                MemberDTO userInfo = service.getUserDeptInfo(user_id);
                String deptName = userInfo != null && userInfo.getDept_name() != null ? 
                    userInfo.getDept_name() : "팀";
                int userDeptIdx = userInfo != null ? userInfo.getDept_idx() : 1;
                
                notiService.sendLinkSaveNotification(
                    user_id,
                    linkName,
                    deptName,
                    userDeptIdx
                );
            } catch (Exception e) {
                log.error("링크 저장 알림 전송 실패: {}", e.getMessage());
            }

            response.put("success", true);
            response.put("data", savedLink);
            response.put("message", "링크 저장 성공");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.info("링크 저장 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "링크 저장 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 링크 목록 조회 API
     */
    @GetMapping("/cloud/link/list")
    public ResponseEntity<Map<String, Object>> getLinkList(
        @RequestParam("user_id") String user_id) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> links = service.getLinkList(user_id);
            
            response.put("success", true);
            response.put("data", links);
            response.put("message", "링크 목록 조회 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.info("링크 목록 조회 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "링크 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 링크 삭제 API
     */
    @DeleteMapping("/cloud/link/delete/{link_idx}")
    public ResponseEntity<Map<String, Object>> deleteLink(@PathVariable int link_idx) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // DB에서 링크 정보 삭제
            boolean deleted = service.deleteLink(link_idx);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "링크 삭제 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "삭제할 링크를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.info("링크 삭제 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "링크 삭제 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 링크 수정 API
     */
    @PutMapping("/cloud/link/update")
    public ResponseEntity<Map<String, Object>> updateLink(
            @RequestParam("linkIdx") int linkIdx,
            @RequestParam("linkName") String linkName,
            @RequestParam("url") String url) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 빈 값 체크
        if (linkName == null || linkName.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "링크 이름을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (url == null || url.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "URL을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // URL 형식 검증 (간단한 검증)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            response.put("success", false);
            response.put("message", "올바른 URL 형식이 아닙니다. (http:// 또는 https://로 시작해야 합니다)");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 링크 정보 설정
            LinkDTO linkDTO = new LinkDTO();
            linkDTO.setLink_idx(linkIdx);
            linkDTO.setLink_name(linkName.trim());
            linkDTO.setUrl(url.trim());
            
            // 서비스에 링크 수정 요청
            LinkDTO updatedLink = service.updateLink(linkDTO);

            response.put("success", true);
            response.put("data", updatedLink);
            response.put("message", "링크 수정 성공");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.info("링크 수정 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "링크 수정 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/cloud/allList")
    public Map<String, Object> ColudAllList(){
    	
    	Map<String, Object> response = new HashMap<>();
    	
    	ArrayList<CloudDTO> cloudList = service.ColudList();
    	ArrayList<LinkDTO> linkList = service.linkList();
    	
    	response.put("cloudList", cloudList);
    	response.put("linkList", linkList);
    	
    	return response;
    }
    
    /**
     * 파일별 다운로드 횟수 조회 API
     */
    @GetMapping("/cloud/download/count")
    public ResponseEntity<Map<String, Object>> getFileDownCount() {
        Map<String, Object> resp = new HashMap<>();
        
        try {
            List<Map<String, Object>> counts = service.getFileDownCount();
            resp.put("success", true);
            resp.put("data", counts);
            resp.put("message", "파일별 다운로드 횟수 조회 성공");
            return ResponseEntity.ok(resp);
            
        } catch (Exception e) {
            log.error("파일별 다운로드 횟수 조회 실패: {}", e.getMessage(), e);
            resp.put("success", false);
            resp.put("message", "파일별 다운로드 횟수 조회 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}

