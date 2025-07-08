package com.bytehub.approval;

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

}
