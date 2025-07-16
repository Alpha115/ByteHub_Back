package com.bytehub.project;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProjectDTO {
	
	private int project_idx;
	private String user_id;
	private String subject;
	private String content;
	private Date start_date;
	private Date end_date;
	private int priority;
	private int progress;
	private int dept_idx;
	
	private List<ProjectFileDTO> files;
	private List<ProjectEmpDTO> members;
}
