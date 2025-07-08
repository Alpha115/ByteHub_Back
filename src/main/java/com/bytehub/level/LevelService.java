package com.bytehub.level;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService {

	private final LevelDAO dao;

	public ArrayList<LevelDTO> lvList(LevelDTO dto) {
		return dao.lvList(dto);
	}

	public boolean lvInsert(LevelDTO dto) {
		int row = dao.lvInsert(dto);
		return row > 0 ? true : false;
	}

	public boolean lvUpdate(LevelDTO dto) {
		int row = dao.lvUpdate(dto);
		return row > 0 ? true : false;
	}

	public boolean lvDelete(LevelDTO dto) {
		int row = dao.lvDelete(dto);
		return row > 0 ? true : false;
	}
	
}
