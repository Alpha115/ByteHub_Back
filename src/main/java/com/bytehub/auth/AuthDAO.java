package com.bytehub.auth;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthDAO {

	int addAuth(Map<String, Object> map);

	int delAuth(Map<String, Object> map);

	ArrayList<AuthDTO> grantId(String user_id);

	String searchAuth(Map<String, Object> map);

}
