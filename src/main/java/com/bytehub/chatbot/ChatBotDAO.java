package com.bytehub.chatbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatBotDAO {

	int insertKeyword(Map<String, Object> param);

	ArrayList<SearchDTO> listKeyword();

	int updateKeyword(SearchDTO dto);

	int delKeyword(SearchDTO dto);

	int faqInsert(FAQDTO dto);

	ArrayList<FAQDTO> faqList(FAQDTO dto);

	List<FAQDTO> faqTop5();

}
