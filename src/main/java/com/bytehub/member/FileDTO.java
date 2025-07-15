package com.bytehub.member;

import lombok.Data;

@Data
public class FileDTO {

    private int file_idx;
    private String ori_filename;
    private String new_filename;
    private String file_type; // enum이지만 String으로 받는 게 일반적입니다
    private int appr_idx;

    public String getOri_filename() {
        return ori_filename;
    }

    public String getFile_type() {
        return file_type;
    }
    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
    public int getAppr_idx() {
        return appr_idx;
    }
    public void setAppr_idx(Integer appr_idx) {
        this.appr_idx = appr_idx;
    }

}
