package com.bytehub.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

	Map<String, Object> resp = null;

	private final EmailService service;
	int randomNum = -1;

	// ---------이메일서비스 초기값---------
	String host = "smtp.gmail.com";
	String port = "587";
	String sender= "levlodia125@gmail.com";
	String key = "xgrk qwtn jakr hqxk";
	String enable = "true";
	Properties props = new Properties();

	// --------임시 비밀번호(6자리) 랜덤 발급 함수---------
	int createPw() {
		randomNum=(int) ((Math.random()*90000)+100000);
		return randomNum;
	}
	
	

	// ---------비상연락망 메일 돌리기---------
	@PostMapping("/emergency")
	public Map<String, Object> emergencySendEmail(@RequestBody Map<String, Object> info) {
		resp = new HashMap<String, Object>();
		Map<String, Object> mail = new HashMap<String, Object>();
		
//		String sender=(String) info.get("sender");	// 이메일 발신자가 있네요...내용에 넣을예정

		mail.put("sender", sender);
		mail.put("receiver", info.get("receiver")); // <입력받은 유저 이메일들(ArrayList<String>)
		mail.put("key", key);
		mail.put("subject", info.get("subject")); // <메일 제목
		mail.put("content", info.get("content")); // <메일 본문

		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", port);
		props.setProperty("mail.smtp.auth", enable);
		props.setProperty("mail.smtp.starttls.enable", enable);
		props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // <Tlsv1.2 버전 추가

		resp.put("msg", service.emergencySendMail(props, mail)); // 반환값: 이메일 발송 메시지를 반환합니다. ({"

		return resp;
	}

}
