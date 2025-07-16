package com.bytehub.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bytehub.member.FileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

	private final ProjectDAO dao;

	 @Value("${spring.servlet.multipart.location}")
	 private String uploadDir;
	
	@Transactional
	public boolean create(ProjectDataDTO data, MultipartFile[] files) {
	    boolean result = dao.insertProject(data.getProj()) > 0;

	    if (result) {
	        int projectIdx = data.getProj().getProject_idx();

	        // 1. 파일 저장 후 연결
	        if (files != null && files.length > 0) {
	            List<FileDTO> savedFiles = saveFiles(files); // 디스크 업로드 + DB 저장
	            for (FileDTO file : savedFiles) {
	                dao.insertProjectFile(projectIdx, file.getFile_idx());
	            }
	        }

	        // 2. 멤버 연결
	        if (data.getUser_id() != null) {
	            for (String userId : data.getUser_id()) {
	                dao.insertProjectEmp(projectIdx, userId);
	            }
	        }
	    }

	    return result;
	}
	
	private List<FileDTO> saveFiles(MultipartFile[] files) {
	    List<FileDTO> list = new ArrayList<>();

	    File dir = new File(uploadDir);
	    if (!dir.exists()) dir.mkdirs();

	    for (MultipartFile mf : files) {
	        try {
	            String ori = mf.getOriginalFilename();
	            String uuid = UUID.randomUUID().toString();
	            String saved = uuid + "_" + ori;

	            File dest = new File(uploadDir,saved);
	            mf.transferTo(dest);

	            FileDTO fileDTO = new FileDTO();
	            fileDTO.setOri_filename(ori);
	            fileDTO.setNew_filename(saved);
	            fileDTO.setFile_type("project"); // 고정
	            dao.insertFile(fileDTO);
	            list.add(fileDTO);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
	        }
	    }

	    return list;
	}

	@Transactional
	public boolean edit(ProjectDataDTO data, MultipartFile[] files) {
	    boolean result = dao.updateProject(data.getProj()) > 0;

	    if (result) {
	        int projectIdx = data.getProj().getProject_idx();

	        // 기존 멤버/파일 연결 모두 삭제
	        dao.deleteProjectEmpByProjectIdx(projectIdx);
	        dao.deleteProjectFilesByProjectIdx(projectIdx);

	        // 멤버 재등록
	        if (data.getUser_id() != null) {
	            for (String userId : data.getUser_id()) {
	                dao.insertProjectEmp(projectIdx, userId);
	            }
	        }
	        // 기존에 선택된 파일 연결 (file_idx로 전달됨)
	        if (data.getFile_idx() != null) {
	            uploadProjectFiles(projectIdx, data.getFile_idx());
	        }

	        // **새 파일 업로드 및 연결 (여기 추가)**
	        if (files != null && files.length > 0) {
	            List<FileDTO> savedFiles = saveFiles(files);
	            for (FileDTO file : savedFiles) {
	                dao.insertProjectFile(projectIdx, file.getFile_idx());
	            }
	        }
	    }
	    return result;
	}

    public void uploadProjectFiles(int projectIdx, int[] fileIdxArr) {
        for (int fileIdx : fileIdxArr) {
            dao.insertProjectFile(projectIdx, fileIdx);
        }
    }

    public Map<String, Object> detail(int idx) {
        Map<String, Object> result = new HashMap<>();
        ProjectDTO proj = dao.selectProjectById(idx);
        List<String> users = dao.selectUsersByProjectIdx(idx);
        List<FileDTO> files = dao.selectFilesByProjectIdx(idx);
        result.put("project", proj);
        result.put("users", users);
        result.put("files", files);
        return result;
    }

    public List<ProjectDTO> list() {
        return dao.selectAllProjects();
    }

	public ArrayList<ProjectFileDTO> fileListByProject() {
		return dao.fileListByProject();
	}

	public ArrayList<ProjectEmpDTO> empListByProject() {
		return dao.empListByProject();
	}

//	@Transactional
//	public boolean delete(int project_idx) {
//		dao.deleteFile(project_idx);
//		int row = dao.delete(project_idx);
//		return row > 0;
//	}
	
	// 프로젝트 아카이브 할 때 deleteProjFromScd 하면 될듯

}
