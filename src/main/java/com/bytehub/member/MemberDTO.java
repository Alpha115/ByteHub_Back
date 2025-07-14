package com.bytehub.member;

import lombok.Data;
import java.util.Date;

@Data
public class MemberDTO {
    private String user_id;
    private int file_idx;
    private int dept_idx;
    private int lv_idx;
    private String password;
    private String name;
    private String email;
    private String gender;
    private Date hire_date;
    private String lv_name;
    private String dept_name;
    private String status;
    private Date hire_end_date;
}
