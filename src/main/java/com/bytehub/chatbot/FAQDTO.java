package com.bytehub.chatbot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FAQDTO {
	
	private int key_idx;
	private String question;
	private String answer;
	private int cnt;
}
