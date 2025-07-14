package com.bytehub.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttDTO {
    
    private int att_idx;           // 근태 idx
    private String user_id;        // 사용자 ID
    private LocalDate att_date;    // 근무 일자
    private LocalDateTime in_time; // 출근 시간
    private LocalDateTime out_time; // 퇴근 시간
    private String att_type;       // 근태 타입 (정상, 지각, 조퇴 등)
    
    private String name;
    private String dept_name;
    
}
