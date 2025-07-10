package com.bytehub.project;

import java.sql.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProjectDTO {
	
	private int project_idx;
	private String user_id;
	private String subject;
	private String content;
	private Date start_date;
	private Date end_date;
	private int priority;

}
