package com.bytehub.approval;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ApprDAO {
    int createAppr(ApprDTO appr);
    int appr_checker(Map<String, Object> param);
    List<ApprLineDTO> getApprLine();
    int updateStatus(Map<String, Object> param);
    List<Map<String, Object>> getMyAppr(String writer_id);
    List<Map<String, Object>> getMyHistory(String checker_id);
}
