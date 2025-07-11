package com.bytehub.schedule;

import java.sql.Date;

import lombok.Data;

@Data
public class ScdDTO {

	private String user_id;
	private String scd_type;
	private int type_idx;
	private String subject;
	// db에는 datetime 형태로 저장되어 있네…
	private Date start_time;
	private Date end_time;
	
}
