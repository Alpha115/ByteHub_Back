package com.bytehub.attendance;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttSettingDTO {

    private int time_set_idx;        // idx
    private String user_id;          // 관리자 ID
    private LocalDateTime set_in_time;   // 출근 설정 시간 
    private LocalDateTime set_out_time;  // 퇴근 설정 시간 
    private int term;                // 유효 시간 


}
