package com.bytehub.board;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardDAO {

	// 게시글 작성
	int postWrite(BoardDTO dto);

	// 게시글 수정
	int postUpdate(BoardDTO dto);

	// 컨트롤러가 게시글 작성자 ID를 가져오기 위한 헬퍼 메서드
	String postWriter(int post_idx);

	// 게시글 삭제
	int postDel(int post_idx);

}
