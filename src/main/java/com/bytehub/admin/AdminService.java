package com.bytehub.admin;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

	private final AdminDAO dao;

	public boolean withdraw(String id) {
		int row = dao.withdraw(id);
		return row > 0;
	}

}
