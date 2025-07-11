package com.bytehub.schedule;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScdService {

	private final SchDAO dao;

	public boolean insert(ScdDTO info) {
		int row = 0;
		row += dao.insert(info);
		return row > 0;
	}

}
