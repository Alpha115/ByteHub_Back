package com.bytehub.approval;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

import lombok.Data;

@Mapper
@Data
public class ApprDTO {

    private int appr_idx;
    private String writer_id;
    private String checker_id;
    private String subject;
    private String content;
    private LocalDateTime appr_date;
    private String appr_type;
    
    // 연차 관련 필드
    private Date vac_start;
    private Date vac_end;

}
