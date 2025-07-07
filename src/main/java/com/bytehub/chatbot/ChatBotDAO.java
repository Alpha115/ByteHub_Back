package com.bytehub.chatbot;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatBotDAO {

	int insertKeyword(Map<String, Object> param);

	ArrayList<SearchDTO> listKeyword();

	int updateKeyword(SearchDTO dto);

	int delKeyword(SearchDTO dto);

}
