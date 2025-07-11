package com.bytehub.project;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.bytehub.schedule.ScdDTO;

@Mapper
public interface ProjectDAO {

	int insertProj(ProjectDTO proj);

	int insertFile(int project_idx, int[] file_idx);

	int insertUser(int project_idx, String[] user_id);
	
	int updateProj(ProjectDTO proj);

	int deleteFile(int project_idx);
	
	int deleteUser(int project_idx);

	int delete(int project_idx);

	Map<String, Object> detail(int idx);

	ArrayList<String> files(int idx);
	
	ArrayList<Map<String, String>> members(int idx);

	ArrayList<Map<String, Object>> list();
	
	// 일정에 자동으로 추가/업데이트/삭제하는 함수
	int insertProjToScd(ProjectDTO proj);
	
	int updateProjToScd(ProjectDTO proj);
	
	int deleteProjFromScd(int idx);
	
}
