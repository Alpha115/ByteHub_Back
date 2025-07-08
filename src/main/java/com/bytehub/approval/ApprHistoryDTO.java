package com.bytehub.approval;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

import lombok.Data;

@Mapper
@Data
public class ApprHistoryDTO {

    private int appr_his_idx;   // 결재 내역 idx (PK)
    private int appr_idx;       // 결재 idx (FK)
    private String checker_id;  // 결재자(라인)
    private String reason;      // 사유
    private String status;      // 결재 구분
    private LocalDateTime check_time; // 결재일
}
