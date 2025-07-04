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

}
