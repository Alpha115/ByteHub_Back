package com.bytehub.member;

import lombok.Data;

@Data
public class FileDTO {

    private int file_idx;
    private String ori_filename;
    private String new_filename;
    private String file_type; // enum이지만 String으로 받는 게 일반적입니다
    private int appr_idx; // NULL 허용 시 Integer, 아니면 int

}
