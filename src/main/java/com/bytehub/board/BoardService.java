package com.bytehub.board;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardService {
		
	private int post_count = 20; // 페이지 당 게시글 수
	
	@Autowired BoardDAO dao;

	// 게시글 작성
	public boolean postWrite(BoardDTO dto) {
		
		// 상단 고정 선택 3개까지 ㄱㄴ
		if (dto.isPinned()) {
	        int pinnedCnt = dao.cntPinned();
	        if (pinnedCnt >= 3) {
	            // 상단 고정 3개 초과 불가
	            return false;
	        }
	    }
		
		int row = dao.postWrite(dto);
		
	    return row > 0;
	}
	
	// 상단 고정 게시글 개수 조
	public int cntPinned() {
	    return dao.cntPinned();
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

	// 게시글 리스트
	public List<BoardDTO> postList(int page) {
		
		int offset = (page - 1) * post_count; // 페이지 시작 위치 계산
		return dao.postList(offset, post_count);
	}

	// 게시글 상세보기
	public BoardDTO postDetail(int idx) {
		return dao.postDetail(idx);
	}

	public boolean AiInsert(SummaryDTO dto) {
		int row = dao.AiInsert(dto);
		return row > 0 ? true : false;
	}

	public ArrayList<SummaryDTO> AiList(SummaryDTO dto) {
		return dao.AiList(dto);
	}

	public boolean AiUpdate(SummaryDTO dto) {
		int row = dao.AiUpdate(dto);
		return row > 0 ? true : false;
	}


}