package com.bytehub.board;

import java.sql.Timestamp;
import java.util.List;

import com.bytehub.board.BoardCategory;

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
	


	// 게시판 종류 getter/setter
    public BoardCategory getCategory() {
        return category;
    }
    public void setCategory(BoardCategory category) {
        this.category = category;
    }
	// 게시글 인덱스 getter/setter
	public int getPost_idx() {
		return post_idx;
	}
	public void setPost_idx(int post_idx) {
		this.post_idx = post_idx;
	}
	// 작성자 getter/setter
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	// 제목 getter/setter
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	// 내용 getter/setter
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	// 상단 고정 getter/setter
	public boolean isPinned() {
		return pinned;
	}
	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}
	// 임시 저장 getter/setter
	public boolean isDraft() {
		return draft;
	}
	public void setDraft(boolean draft) {
		this.draft = draft;
	}
	// 작성시간 getter/setter
	public Timestamp getReg_date() {
		return reg_date;
	}
	public void setReg_date(Timestamp reg_date) {
		this.reg_date = reg_date;
	}
	// 파일 인덱스 getter/setter
	public Integer getFile_idx() {
		return file_idx;
	}
	public void setFile_idx(Integer file_idx) {
		this.file_idx = file_idx;
	}
	// 참석자 목록 getter/setter
	public List<String> getAttendees() {
		return attendees;
	}
	public void setAttendees(List<String> attendees) {
		this.attendees = attendees;
	}

	
	

}
