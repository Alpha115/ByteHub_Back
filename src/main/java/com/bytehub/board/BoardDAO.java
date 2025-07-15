package com.bytehub.board;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bytehub.member.FileDTO;

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

	// 게시글 리스트
	List<BoardDTO> postList(@Param("offset") int offset, @Param("post_count") int post_count);

	// 상단 고정 3개까지 ㄱㄴ
	int cntPinned();

	// 게시글 상세보기
	BoardDTO postDetail(int idx);

	int AiInsert(SummaryDTO dto);

	ArrayList<SummaryDTO> AiList(SummaryDTO dto);

	int AiUpdate(SummaryDTO dto);

	// 게시판 파일 저장
	int insertBoardFile(FileDTO fileDTO);
	
	// 파일 정보 조회
	FileDTO getFileByIdx(int file_idx);

	// 참석자 정보 저장
	int insertAttendee(@Param("user_id") String user_id, @Param("scd_type") String scd_type, @Param("type_idx") int type_idx);
	
	// 게시글의 참석자 목록 조회
	List<String> getAttendeesByPostIdx(@Param("post_idx") int post_idx);
	
	// 게시글의 기존 참석자 삭제
	int deleteAttendeesByPostIdx(@Param("post_idx") int post_idx);
}
