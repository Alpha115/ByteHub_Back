package com.bytehub.auth;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

	private final AuthDAO dao;

	@Transactional
	public boolean grant(ArrayList<Map<String, Object>> info) {
		int row = 0;
		// 여기에서 update 하기
		// 중복되는거 좀 없었으면 좋겠는디...
		for (Map<String, Object> map : info) {
			if((boolean) map.get("checked")) {
				row+=dao.addAuth(map);
			}
			else {
				row+=dao.delAuth(map);
			}
		}
		return row > 0;
	}
	

	public ArrayList<AuthDTO> grantId(String user_id) {
		return dao.grantId(user_id);
	}

}
