package com.bytehub.chatbot;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatBotService {
	
	private final ChatBotDAO dao;

	public boolean insertKeyword(Map<String, Object> param) {
		
		int row = dao.insertKeyword(param);
		
		return row > 0 ? true : false;
	}

	public ArrayList<SearchDTO> listKeyword() {
		return dao.listKeyword();
	}

	public boolean updateKeyword(SearchDTO dto) {
		int row = dao.updateKeyword(dto);
		return row > 0 ? true : false;
	}

	public boolean delKeyword(SearchDTO dto) {
		int row = dao.delKeyword(dto);
		return row > 0 ? true : false;
	}

}
