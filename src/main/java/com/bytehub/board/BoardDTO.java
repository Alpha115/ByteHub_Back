package com.bytehub.board;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO {
	
	private int post_idx; // 게시글 인덱스
	private String user_id; // 작성자
	private String subject; // 제목
	private String content; // 내용
	private boolean pinned; // 상단 고정 여부
	private boolean draft; // 임시 저장
	private Timestamp reg_date; // 작성시간
	private Integer file_idx; // 파일 인덱스
	private List<String> attendees; // 참석자 목록 (회의록용)

	private BoardCategory category; // 게시판 종류
	
}
