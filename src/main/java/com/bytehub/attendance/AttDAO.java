package com.bytehub.attendance;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttDAO {
    int insertAttendance(AttDTO dto);
    int insertAttHistory(AttHistoryDTO dto);
}
