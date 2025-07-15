package com.bytehub.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CloudService {
    
    @Autowired
    private CloudDAO dao;
    
    public CloudDTO saveFile(CloudDTO cloudDTO) {
        try {
            // 업로드 시간 설정
            cloudDTO.setCreated_at(new Timestamp(System.currentTimeMillis()));
            
            // DB에 파일 정보 저장
            int result = dao.insertCloudFile(cloudDTO);
            
            if (result > 0) {
                log.info("파일 업로드 성공: {}", cloudDTO.getFilename());
                return cloudDTO;
            } else {
                throw new RuntimeException("파일 저장 실패");
            }
            
        } catch (Exception e) {
            log.error("파일 저장 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getFileList(int deptIdx) {
        try {
            return dao.getFileList(deptIdx);
        } catch (Exception e) {
            log.error("파일 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 목록 조회 실패: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getFileInfo(int fileIdx) {
        try {
            return dao.getFileInfo(fileIdx);
        } catch (Exception e) {
            log.error("파일 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 정보 조회 실패: " + e.getMessage());
        }
    }
    
    public boolean deleteFile(int fileIdx) {
        try {
            // DB에서 파일 정보 삭제
            int result = dao.deleteCloudFile(fileIdx);
            
            if (result > 0) {
                log.info("파일 삭제 성공: file_idx = {}", fileIdx);
                return true;
            } else {
                log.error("파일 삭제 실패: file_idx = {}", fileIdx);
                return false;
            }
            
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
        }
    }
}

