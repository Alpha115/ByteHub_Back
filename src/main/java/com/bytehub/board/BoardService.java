package com.bytehub.board;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	private int post_count = 5; // 페이지 당 게시글 수
	
	@Autowired BoardDAO dao;

	// 게시글 작성
	public boolean postWrite(BoardDTO dto) {
		
		int row = dao.postWrite(dto);
	    return row > 0;
	}

	// 게시글 수정
	public boolean postUpdate(BoardDTO dto) {
		int row = dao.postUpdate(dto);
	    return row>0;
	}
	
	// 컨트롤러가 게시글 작성자 ID를 가져오기 위한 헬퍼 메소드
	public String postWriter(int post_idx) {
		return dao.postWriter(post_idx);
	}

	// 게시글 삭제
	public boolean postDel(BoardDTO dto) {
		int row = dao.postDel(dto.getPost_idx());
		return row > 0;
	}

}
