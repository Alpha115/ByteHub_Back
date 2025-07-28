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
import com.bytehub.schedule.ScdDTO;
import com.bytehub.notification.NotiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

	private final ProjectDAO dao;
	private final NotiService notiService;

	 @Value("${spring.servlet.multipart.location}")
	 private String uploadDir;
	
	@Transactional
	public boolean create(ProjectDataDTO data, MultipartFile[] files) {
	    boolean result = dao.insertProject(data.getProj()) > 0;
        ScdDTO scd = new ScdDTO();

	    if (result) {
	        
	        int projectIdx = data.getProj().getProject_idx();
	        
	        // 1. 파일 저장 후 연결
	        if (files != null && files.length > 0) {
	            List<FileDTO> savedFiles = saveFiles(files); // 디스크 업로드 + DB 저장
	            for (FileDTO file : savedFiles) {
	                dao.insertProjectFile(projectIdx, file.getFile_idx());
	            }
	        }

	        // 2. 멤버 연결 및 초대 알림 전송
	        if (data.getUser_id() != null) {
	            for (String userId : data.getUser_id()) {
	                dao.insertProjectEmp(projectIdx, userId);
	                
	                // 프로젝트 생성자와 다른 사용자에게만 알림 전송
	                if (!userId.equals(data.getProj().getUser_id())) {
	                    try {
	                        notiService.sendProjectInviteNotification(
	                            userId,
	                            data.getProj().getSubject(),
	                            data.getProj().getUser_id() // 프로젝트 생성자 ID
	                        );
	                    } catch (Exception e) {
	                        log.info("프로젝트 초대 알림 전송 실패: {}", e.getMessage());
	                    }
	                }
	            }
	        }
	        
	    	// 3. 일정에 추가
	        scd.setUser_id(data.getProj().getUser_id());
	        scd.setScd_type("project");
	        scd.setType_idx(projectIdx);
	        scd.setSubject(data.getProj().getSubject());
	        scd.setStart_date(data.getProj().getStart_date());
	        scd.setEnd_date(data.getProj().getEnd_date());
	        dao.insertScd(scd);
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
	    ScdDTO scd=new ScdDTO();

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
	        
	        //일정 변경
	        scd.setUser_id(data.getProj().getUser_id());
	        scd.setType_idx(projectIdx);
	        scd.setSubject(data.getProj().getSubject());
	        scd.setStart_date(data.getProj().getStart_date());
	        scd.setEnd_date(data.getProj().getEnd_date());
	        dao.updateScd(scd);
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

	@Transactional
	public boolean projectDelete(ProjectDTO dto) {
		int row = dao.projectDelete(dto);
		if(row > 0) {
			dao.deleteScd(dto.getProject_idx());
		}
		return row > 0 ? true : false;
	}

//	@Transactional
//	public boolean delete(int project_idx) {
//		dao.deleteFile(project_idx);
//		int row = dao.delete(project_idx);
//		return row > 0;
//	}
	
	// 프로젝트 아카이브 할 때 deleteProjFromScd 하면 될듯

}
