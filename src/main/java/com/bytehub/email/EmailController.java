package com.bytehub.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class EmailController {

	Map<String, Object> resp = null;
	private final EmailService service;

	// ---------이메일서비스 초기값---------
	String host = "smtp.gmail.com";
	String port = "587";
	String sender = "levlodia125@gmail.com";
	String key = "zsum oanh jwxb uybi";
	String enable = "true";
	Properties props = new Properties();

	
	
	
	// ---------비상연락망 메일 돌리기---------
	@PostMapping("/email/emergency")
	public Map<String, Object> emergencySendEmail(@RequestBody Map<String, Object> info) {
		resp = new HashMap<String, Object>();
		Map<String, Object> mail = new HashMap<String, Object>();

		mail.put("sender", sender);
		mail.put("receiver", info.get("receiver")); // <입력받은 유저 이메일들(ArrayList<String>)
		mail.put("key", key);
		mail.put("subject", info.get("subject"));	// <메일 제목
		mail.put("content", info.get("content"));	// <메일 본문

		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", port);
		props.setProperty("mail.smtp.auth", enable);
		props.setProperty("mail.smtp.starttls.enable", enable);
		props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // <Tlsv1.2 버전 추가
		
		resp.put("msg", service.emergencySendMail());	// 반환값: 이메일 발송 메시지를 반환합니다. ({"
		
		return resp;
	}

}
