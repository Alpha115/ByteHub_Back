package com.bytehub.attendance;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttHistoryDTO {
    private int cert_his_idx; // 근태 히스토리 idx
    private Integer att_idx; // 근태 idx (nullable)
    private int cert_no; // 인증번호
    private Boolean cert_status; // 인증 성공 여부
    private LocalDateTime cert_time; // 인증 시각
} 