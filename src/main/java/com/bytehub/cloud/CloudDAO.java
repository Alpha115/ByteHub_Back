package com.bytehub.cloud;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CloudDAO {
    
    // 파일 업로드 시 cloud 테이블에 저장
    int insertCloudFile(CloudDTO cloudDTO);
    
    // 파일 목록 조회 (member 테이블과 JOIN)
    List<Map<String, Object>> getFileList(int deptIdx);
    
    // 개별 파일 정보 조회
    Map<String, Object> getFileInfo(int fileIdx);
    
    // 파일 삭제
    int deleteCloudFile(int fileIdx);
    
    // 다운로드 로그 저장
    int insertDownLog(DownLogDTO downLogDTO);
    
    // 모든 부서 목록 조회
    List<Map<String, Object>> getAllDepartments();
    
    // 링크 저장
    int insertLink(LinkDTO linkDTO);
    
    // 링크 목록 조회
    List<Map<String, Object>> getLinkList(String user_id);
    
    // 링크 삭제
    int deleteLink(int linkIdx);
    
    // 링크 수정
    int updateLink(LinkDTO linkDTO);
    
    // 개별 링크 정보 조회
    Map<String, Object> getLinkInfo(int linkIdx);
}
