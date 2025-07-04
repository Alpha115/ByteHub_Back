package com.bytehub.admin;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminDAO {
	
	
	// 관리자의 아이디로 직원을 퇴사처리하는 함수입니다. toggle 방식
	int withdraw(String id);

}
