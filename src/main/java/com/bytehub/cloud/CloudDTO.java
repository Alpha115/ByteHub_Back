package com.bytehub.cloud;

import java.sql.Timestamp;

public class CloudDTO {

    private int file_idx;
    private int dept_idx;
    private String filename;
    private String user_id;
    private Timestamp created_at;

    public int getFile_idx() {
        return file_idx;
    }

    public void setFile_idx(int file_idx) {
        this.file_idx = file_idx;
    }

    public int getDept_idx() {
        return dept_idx;
    }

    public void setDept_idx(int dept_idx) {
        this.dept_idx = dept_idx;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}   
