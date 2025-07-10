package com.bytehub.board;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryDTO {
	
	private int sum_idx;
	private int post_idx;
	private String summary;
	private Date edit_time;

}
