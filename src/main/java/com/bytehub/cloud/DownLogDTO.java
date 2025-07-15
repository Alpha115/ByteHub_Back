package com.bytehub.cloud;

import java.sql.Timestamp;

public class DownLogDTO {

    private int log_id;
    private int file_idx;
    private String user_id;
    private Timestamp down_time;

    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public int getFile_idx() {
        return file_idx;
    }

    public void setFile_idx(int file_idx) {
        this.file_idx = file_idx;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Timestamp getDown_time() {
        return down_time;
    }

    public void setDown_time(Timestamp down_time) {
        this.down_time = down_time;
    }
}
