package com.bytehub.board;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytehub.member.FileDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardService {
		
	private int post_count = 20; // 페이지 당 게시글 수
	
	@Autowired BoardDAO dao;

	// 게시글 작성
	public boolean postWrite(BoardDTO dto, List<FileDTO> fileList) {
		
		log.info("=== 게시글 작성 시작 ===");
		log.info("입력된 DTO: {}", dto);
		log.info("파일 리스트 크기: {}", fileList != null ? fileList.size() : 0);
		
		// 파일 먼저 저장 (file_idx 얻기 위해)
		Integer file_idx = null;
		if (fileList != null && !fileList.isEmpty()) {
			log.info("파일 저장 시작 - 파일 개수: {}", fileList.size());
            
            // 첫 번째 파일만 저장 (board.file_idx는 단일 파일 참조)
            FileDTO fileDTO = fileList.get(0);
            fileDTO.setFile_type("board");
            
            log.info("파일 저장 중: {}", fileDTO);
            int fileResult = dao.insertBoardFile(fileDTO);
            
            if (fileResult > 0) {
                file_idx = fileDTO.getFile_idx(); // 자동 생성된 file_idx 가져오기
                log.info("파일 저장 완료 - file_idx: {}", file_idx);
            } else {
                log.error("파일 저장 실패");
                return false;
            }
        }
		
		// 게시글에 file_idx 설정
		if (file_idx != null) {
			dto.setFile_idx(file_idx);
		} else {
			dto.setFile_idx(null); // 명시적으로 null 설정
		}
		
		// 상단 고정 선택 3개까지 ㄱㄴ (게시글 저장 전에 체크)
		if (dto.isPinned()) {
	        int pinnedCnt = dao.cntPinned();
	        log.info("현재 상단 고정 개수: {}", pinnedCnt);
	        if (pinnedCnt >= 3) {
	            // 상단 고정 3개 초과 불가
	            log.warn("상단 고정 3개 초과로 실패");
	            return false;
	        }
	    }
		
		int row = dao.postWrite(dto);
        Integer post_idx = dto.getPost_idx();
		
		log.info("게시글 저장 결과: row={}, post_idx={}", row, post_idx);
		
		// 게시글 저장 실패 시 바로 리턴
		if (row <= 0 || post_idx == null) {
			log.error("게시글 저장 실패 - row: {}, post_idx: {}", row, post_idx);
			return false;
		}
		
		log.info("=== 게시글 작성 완료 ===");
	    return true; // 게시글 저장 성공
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