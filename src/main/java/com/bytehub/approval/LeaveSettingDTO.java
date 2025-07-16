package com.bytehub.approval;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LeaveSettingDTO {
    
    private int leaveSetIdx;        // leave_set_idx
    private int year;               // 적용 년도
    private int newEmpBase;         // 신규직원(1년미만) 월당 연차
    private int existingEmpBase;    // 기존직원(1년이상) 기본 연차
    private int annualIncrement;    // 근속년수당 추가 연차
    private int maxAnnual;          // 최대 연차 한도
    private LocalDateTime createdDate;   // 생성일시
    private LocalDateTime updatedDate;   // 수정일시
    
} 