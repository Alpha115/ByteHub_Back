package com.bytehub.schedule;

import java.util.ArrayList;

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

	public ArrayList<ScdDTO> total() {

		return dao.total();
	}

	public boolean edit(ScdDTO info) {
		int row = dao.update(info);
		return row > 0;
	}
	// 오버로드
	public boolean delete(int idx) {
		int row = dao.delete(idx);
		return row > 0;
	}

}
