package com.bytehub.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bytehub.member.FileDTO;

@Mapper
public interface ProjectDAO {

	  	int insertProject(ProjectDTO proj);
	    int updateProject(ProjectDTO proj);

	    ProjectDTO selectProjectById(int project_idx);
	    List<ProjectDTO> selectAllProjects();

	    int insertProjectEmp(@Param("project_idx") int project_idx, @Param("user_id") String user_id);
	    int deleteProjectEmpByProjectIdx(int project_idx);
	    List<String> selectUsersByProjectIdx(int project_idx);

	    int insertProjectFile(@Param("project_idx") int project_idx, @Param("file_idx") int file_idx);
	    int deleteProjectFilesByProjectIdx(int project_idx);
	    List<FileDTO> selectFilesByProjectIdx(int project_idx);

	    // 파일 저장 (파일 업로드 API에서 사용)
	    int insertFile(FileDTO file);
	    FileDTO selectFileById(int file_idx);
		ArrayList<ProjectFileDTO> fileListByProject();
		ArrayList<ProjectEmpDTO> empListByProject();
	
}
