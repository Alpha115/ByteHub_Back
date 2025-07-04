package com.bytehub.board;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardDAO {

	// 게시글 작성
	int postWrite(BoardDTO dto);

}
