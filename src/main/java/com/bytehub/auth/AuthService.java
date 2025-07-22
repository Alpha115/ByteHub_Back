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
		// 중복되는거 좀 없었으면 좋겠는디...
		try {
			for (Map<String, Object> map : info) {
				if ((boolean) map.get("checked")) {
					if (dao.searchAuth(map).equals("0")) { // 해당 권한이 존재하지 않을 경우
						row += dao.addAuth(map);
					}
				}
				else {
					log.info("deleted.");
					row+=dao.delAuth(map);
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;	
	}
	

	public ArrayList<AuthDTO> grantId(String user_id) {
		return dao.grantId(user_id);
	}


	public boolean paeneol(String user_id) {
		int row = dao.paeneol(user_id);
		return row > 0;
	}

}
