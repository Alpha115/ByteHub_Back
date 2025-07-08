package com.bytehub.approval;

import org.apache.ibatis.annotations.Mapper;

import lombok.Data;

@Mapper
@Data
public class ApprLineDTO {
    private int step;
    private int lv_idx;
    private String user_id; // nullable, 특정 결재자 지정 가능
}
