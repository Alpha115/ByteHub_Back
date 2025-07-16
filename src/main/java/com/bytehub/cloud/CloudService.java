package com.bytehub.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
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
            log.info("파일 저장 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getFileList(int deptIdx) {
        try {
            return dao.getFileList(deptIdx);
        } catch (Exception e) {
            log.info("파일 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 목록 조회 실패: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getFileInfo(int fileIdx) {
        try {
            return dao.getFileInfo(fileIdx);
        } catch (Exception e) {
            log.info("파일 정보 조회 실패: {}", e.getMessage(), e);
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
                log.info("파일 삭제 실패: file_idx = {}", fileIdx);
                return false;
            }
            
        } catch (Exception e) {
            log.info("파일 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
        }
    }
    
    public void saveDownLog(int fileIdx, String userId) {
        try {
            DownLogDTO downLogDTO = new DownLogDTO();
            downLogDTO.setFile_idx(fileIdx);
            downLogDTO.setUser_id(userId);
            downLogDTO.setDown_time(new Timestamp(System.currentTimeMillis()));
            
            int result = dao.insertDownLog(downLogDTO);
            
            if (result > 0) {
                log.info("다운로드 로그 저장 성공: file_idx = {}, user_id = {}", fileIdx, userId);
            } else {
                log.info("다운로드 로그 저장 실패: file_idx = {}, user_id = {}", fileIdx, userId);
            }
            
        } catch (Exception e) {
            log.info("다운로드 로그 저장 실패: {}", e.getMessage(), e);
            // 로그 저장 실패는 다운로드 자체를 막지 않도록 예외를 던지지 않음
        }
    }
    
    public List<Map<String, Object>> getAllDepartments() {
        try {
            return dao.getAllDepartments();
        } catch (Exception e) {
            log.info("부서 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("부서 목록 조회 실패: " + e.getMessage());
        }
    }
    
    public LinkDTO saveLink(LinkDTO linkDTO) {
        try {
            // DB에 링크 정보 저장
            int result = dao.insertLink(linkDTO);
            
            if (result > 0) {
                log.info("링크 저장 성공: {}", linkDTO.getLink_name());
                return linkDTO;
            } else {
                throw new RuntimeException("링크 저장 실패");
            }
            
        } catch (Exception e) {
            log.info("링크 저장 실패: {}", e.getMessage(), e);
            throw new RuntimeException("링크 저장 실패: " + e.getMessage());
        }
    }
    
    public List<Map<String, Object>> getLinkList(String userId) {
        try {
            return dao.getLinkList(userId);
        } catch (Exception e) {
            log.info("링크 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("링크 목록 조회 실패: " + e.getMessage());
        }
    }
    
    public boolean deleteLink(int linkIdx) {
        try {
            // DB에서 링크 정보 삭제
            int result = dao.deleteLink(linkIdx);
            
            if (result > 0) {
                log.info("링크 삭제 성공: link_idx = {}", linkIdx);
                return true;
            } else {
                log.info("링크 삭제 실패: link_idx = {}", linkIdx);
                return false;
            }
            
        } catch (Exception e) {
            log.info("링크 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("링크 삭제 실패: " + e.getMessage());
        }
    }
    
    public LinkDTO updateLink(LinkDTO linkDTO) {
        try {
            // DB에서 링크 정보 수정
            int result = dao.updateLink(linkDTO);
            
            if (result > 0) {
                log.info("링크 수정 성공: link_idx = {}", linkDTO.getLink_idx());
                return linkDTO;
            } else {
                throw new RuntimeException("링크 수정 실패");
            }
            
        } catch (Exception e) {
            log.info("링크 수정 실패: {}", e.getMessage(), e);
            throw new RuntimeException("링크 수정 실패: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getLinkInfo(int linkIdx) {
        try {
            return dao.getLinkInfo(linkIdx);
        } catch (Exception e) {
            log.info("링크 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("링크 정보 조회 실패: " + e.getMessage());
        }
    }
}

