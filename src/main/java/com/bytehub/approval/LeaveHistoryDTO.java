package com.bytehub.approval;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public class LeaveHistoryDTO {
	
	private int leave_his_idx;   // 연차 히스토리 idx
    private String writer_id;     // 사용자 ID
    private int appr_idx;     // 결재 idx
    private Date years;           // 기준 연도
    private float remain_days;    // 잔여 연차 (ex: 1.0, 0.5 등)
    
	public int getLeave_his_idx() {
		return leave_his_idx;
	}
	public void setLeave_his_idx(int leave_his_idx) {
		this.leave_his_idx = leave_his_idx;
	}
	public String getWriter_id() {
		return writer_id;
	}
	public void setWriter_id(String writer_id) {
		this.writer_id = writer_id;
	}
	public int getAppr_idx() {
		return appr_idx;
	}
	public void setAppr_idx(int appr_idx) {
		this.appr_idx = appr_idx;
	}
	public Date getYears() {
		return years;
	}
	public void setYears(Date years) {
		this.years = years;
	}
	public float getRemain_days() {
		return remain_days;
	}
	public void setRemain_days(float remain_days) {
		this.remain_days = remain_days;
	}
    
    

}
