package com.bytehub.board;

import java.util.Date;

public class LeaveHistoryDTO {
    private int leave_his_idx; // PK
    private String writer_id;
    private Integer appr_idx;
    private Date years;
    private Float remain_days;

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
    public Integer getAppr_idx() {
        return appr_idx;
    }
    public void setAppr_idx(Integer appr_idx) {
        this.appr_idx = appr_idx;
    }
    public Date getYears() {
        return years;
    }
    public void setYears(Date years) {
        this.years = years;
    }
    public Float getRemain_days() {
        return remain_days;
    }
    public void setRemain_days(Float remain_days) {
        this.remain_days = remain_days;
    }
} 