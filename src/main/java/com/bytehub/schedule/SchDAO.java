package com.bytehub.schedule;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchDAO {

	int insert(ScdDTO info);

	// start_time 기준으로 정렬 기능 有
	ArrayList<ScdDTO> total();

	// user_id는 변경불가
	int update(ScdDTO info);

	int delete(int type_idx, String scd_type);

	// 같은제목이면 다삭제되니 주의
	int deleteBySubject(String subject);

	int today(String subject);

}
