package com.bytehub.board;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bytehub.member.FileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
		
	private int post_count = 20; // 페이지 당 게시글 수
	
	private final BoardDAO dao;

	// 게시판 파일 저장
	public int insertBoardFile(FileDTO fileDTO) {
		return dao.insertBoardFile(fileDTO);
	}
	
	// 파일 정보 조회
	public FileDTO getFileByIdx(int file_idx) {
		return dao.getFileByIdx(file_idx);
	}

	// 게시글 작성
	public boolean postWrite(BoardDTO dto, List<FileDTO> fileList) {
		
		log.info("=== 게시글 작성 시작 ===");
		log.info("입력된 DTO: {}", dto);
		log.info("파일 file_idx: {}", dto.getFile_idx());
		
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
		
		// 참석자 정보 저장 (회의록인 경우에만)
		if (dto.getCategory() == BoardCategory.MEETING && dto.getAttendees() != null && !dto.getAttendees().isEmpty()) {
			log.info("참석자 정보 저장 시작 - post_idx: {}, attendees: {}", post_idx, dto.getAttendees());
			boolean attendeeResult = saveAttendees(post_idx, dto.getAttendees());
			if (!attendeeResult) {
				log.warn("참석자 정보 저장 실패하였으나 게시글은 저장됨");
			}
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
		
		log.info("=== 게시글 수정 시작 ===");
		log.info("수정할 DTO: {}", dto);
		
		int row = dao.postUpdate(dto);
		
		// 게시글 수정 성공 시 참석자 정보도 수정 (회의록인 경우에만)
		if (row > 0 && dto.getCategory() == BoardCategory.MEETING) {
			log.info("회의록 참석자 정보 수정 시작 - post_idx: {}", dto.getPost_idx());
			
			// 기존 참석자 삭제
			boolean deleteResult = deleteAttendeesByPostIdx(dto.getPost_idx());
			log.info("기존 참석자 삭제 결과: {}", deleteResult);
			
			// 새 참석자 저장
			if (dto.getAttendees() != null && !dto.getAttendees().isEmpty()) {
				boolean attendeeResult = saveAttendees(dto.getPost_idx(), dto.getAttendees());
				log.info("새 참석자 저장 결과: {}", attendeeResult);
				if (!attendeeResult) {
					log.warn("참석자 정보 수정 실패하였으나 게시글은 수정됨");
				}
			}
		}
		
		log.info("=== 게시글 수정 완료 ===");
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
		List<BoardDTO> boardList = dao.postList(offset, post_count);
		
		// 각 게시글이 회의록인 경우 참석자 정보도 함께 조회
		log.info("===== 게시글 리스트 참석자 정보 조회 시작 =====");
		log.info("총 게시글 수: {}", boardList.size());
		
		for (BoardDTO board : boardList) {
			log.info("게시글 처리 - post_idx: {}, category: {}", board.getPost_idx(), board.getCategory());
			if (board.getCategory() == BoardCategory.MEETING) {
				List<String> attendees = getAttendeesByPostIdx(board.getPost_idx());
				board.setAttendees(attendees);
				log.info("회의록 참석자 설정 - post_idx: {}, 참석자: {}", board.getPost_idx(), attendees);
			} else {
				log.info("일반 게시글 (참석자 없음) - post_idx: {}", board.getPost_idx());
			}
		}
		
		log.info("===== 게시글 리스트 참석자 정보 조회 완료 =====");
		
		return boardList;
	}

	// 게시글 상세보기
	public BoardDTO postDetail(int idx) {
		BoardDTO board = dao.postDetail(idx);
		
		// 회의록인 경우 참석자 정보도 함께 조회
		if (board != null && board.getCategory() == BoardCategory.MEETING) {
			List<String> attendees = getAttendeesByPostIdx(idx);
			board.setAttendees(attendees);
			log.info("게시글 상세 조회 - post_idx: {}, 참석자: {}", idx, attendees);
		}
		
		return board;
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

	// 참석자 정보 저장
	public boolean saveAttendees(int post_idx, List<String> attendees) {
		try {
			for (String attendee : attendees) {
				if (attendee != null && !attendee.trim().isEmpty()) {
					int result = dao.insertAttendee(attendee.trim(), "MEETING", post_idx);
					dao.grantAttendee(attendee, "board", post_idx);
					if (result <= 0) {
						log.error("참석자 저장 실패: {}", attendee);
						return false;
					}
				}
			}
			log.info("모든 참석자 저장 완료 - post_idx: {}", post_idx);
			return true;
		} catch (Exception e) {
			log.error("참석자 저장 중 오류 발생: {}", e.getMessage(), e);
			return false;
		}
	}

	// 게시글의 참석자 목록 조회
	public List<String> getAttendeesByPostIdx(int post_idx) {
		return dao.getAttendeesByPostIdx(post_idx);
	}

	// 게시글의 기존 참석자 삭제
	public boolean deleteAttendeesByPostIdx(int post_idx) {
		try {
			int result = dao.deleteAttendeesByPostIdx(post_idx);
			dao.deleteAttendeesAuth(post_idx);
			log.info("참석자 삭제 완료 - post_idx: {}, 삭제된 행 수: {}", post_idx, result);
			return true; // 삭제할 참석자가 없어도 성공으로 처리
		} catch (Exception e) {
			log.error("참석자 삭제 중 오류 발생: {}", e.getMessage(), e);
			return false;
		}
	}

	public List<BoardDTO> allList() {
	    List<BoardDTO> boardList = dao.allList();

	    for (BoardDTO board : boardList) {
	        if (board.getCategory() == BoardCategory.MEETING) {
	            List<String> attendees = getAttendeesByPostIdx(board.getPost_idx());
	            board.setAttendees(attendees);
	        } else {
	        }
	    }

	    return boardList;
	}


}