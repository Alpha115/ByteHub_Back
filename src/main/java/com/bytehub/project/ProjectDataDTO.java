package com.bytehub.project;

import lombok.Data;

@Data
public class ProjectDataDTO {
	
	private ProjectDTO proj;
	private int file_idx[];	// file_idx는 여러개가 가능
	private String user_id[];

}
