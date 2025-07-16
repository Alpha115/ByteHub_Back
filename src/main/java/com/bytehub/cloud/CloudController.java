package com.bytehub.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
public class CloudController {

    @Autowired CloudService service;

    // 파일 크기 제한 (100MB)
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;
    
    // 파일 저장 경로 (application.properties 설정 사용)
    private static final String UPLOAD_DIR = "C:/upload/cloud";

    /**
     * 파일 업로드 API
     * - multipart/form-data 형식으로 파일을 업로드 받음
     * - 파일 크기 제한: 10MB
     * - 업로드 시 user_id, dept_idx, 파일명, 시간 정보 저장
     */
    @PostMapping("/cloud/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("expireDate") String expireDate,
            @RequestParam("deptIdx") int deptIdx,
            @RequestParam("userId") String userId) {
        
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
            cloudDTO.setUserId(userId);
            
            // 서비스에 파일 저장 요청
            CloudDTO savedFile = service.saveFile(cloudDTO);

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
            @RequestParam("userId") String userId) {
        try {
            // 파일 정보 조회
            Map<String, Object> fileInfo = service.getFileInfo(file_idx);
            if (fileInfo == null) {
                log.error("파일 정보를 찾을 수 없음: file_idx = {}", file_idx);
                return ResponseEntity.notFound().build();
            }
            
            String filename = (String) fileInfo.get("filename");
            String filePath = UPLOAD_DIR + "/" + filename;
            File file = new File(filePath);
            
            log.info("다운로드 요청 - file_idx: {}, filename: {}, filePath: {}, exists: {}, size: {} bytes", 
                    file_idx, filename, filePath, file.exists(), file.exists() ? file.length() : 0);
            
            if (!file.exists()) {
                log.error("파일이 존재하지 않음: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 다운로드 로그 저장
            service.saveDownLog(file_idx, userId);
            
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
            log.error("파일 다운로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 파일 삭제 API
     */
    @DeleteMapping("/cloud/delete/{file_idx}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable int file_idx) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // DB에서 파일 정보 삭제
            boolean deleted = service.deleteFile(file_idx);
            
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
            log.error("파일 목록 조회 실패: {}", e.getMessage(), e);
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
            log.error("부서 목록 조회 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "부서 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

