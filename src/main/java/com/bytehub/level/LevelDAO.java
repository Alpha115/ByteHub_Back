package com.bytehub.level;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LevelDAO {

	ArrayList<LevelDTO> lvList(LevelDTO dto);

	int lvInsert(LevelDTO dto);

	int lvUpdate(LevelDTO dto);

	int lvDelete(LevelDTO dto);

}
