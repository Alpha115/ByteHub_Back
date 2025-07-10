package com.bytehub.project;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectDAO {

	int insertProj(ProjectDTO proj);

	int insertFile(int project_idx, int[] file_idx);
	
}
