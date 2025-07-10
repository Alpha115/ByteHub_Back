package com.bytehub.admin;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminDAO {
	
	
	// 관리자의 아이디로 직원을 퇴사처리하는 함수입니다. toggle 방식
	int withdraw(String id);

	// 관리자의 아이디로 직원의 리스트(MemberDTO, array)를 불러오는 함수입니다.
	ArrayList<Map<String, Object>> list();
	

}
