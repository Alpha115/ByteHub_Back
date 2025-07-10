package com.bytehub.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttService {
    private final AttDAO dao;

    public int insertAttendance(AttDTO dto) {
        return dao.insertAttendance(dto);
    }

    public int insertAttHistory(AttHistoryDTO dto) {
        return dao.insertAttHistory(dto);
    }
}
