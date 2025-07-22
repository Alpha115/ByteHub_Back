package com.bytehub.schedule;

import java.sql.Date;

import lombok.Data;

@Data
public class ScdDTO {

	private String user_id;
	private String scd_type;
	private int type_idx;
	private String subject;
	private Date start_date;
	private Date end_date;
	
}
