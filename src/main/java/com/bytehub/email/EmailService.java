package com.bytehub.email;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	// 비상연락망단체메일보내기
	public String emergencySendMail(Properties props, Map<String, Object> mail) {

		String sender = (String) mail.get("sender");
		String key = (String) mail.get("key");
		@SuppressWarnings("unchecked")
		ArrayList<String> receivers = (ArrayList<String>) mail.get("receiver");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, key);
			}
		});

		Message msg = new MimeMessage(session);

		try {
			for (String to : receivers) {
				msg.setFrom(new InternetAddress(sender));
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
				msg.setSubject((String) mail.get("subject"));
				msg.setText((String) mail.get("content"));
				
				Transport.send(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "이메일 발송에 실패했습니다.(exception)";
		}

		return "이메일을 발송했습니다.";
	}
	    // 여까지 하면 되실듯?

	// 임시 비밀번호 이메일 발송 (비동기)
	@Async
	public void sendTempPasswordAsync(String to, String tempPassword) {
		log.info("임시 비밀번호 이메일 발송 시작 - 수신자: {}", to);
		
		try {
			Properties props = new Properties();
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.smtp.port", "587");
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.ssl.protocols", "TLSv1.2");
			
			Map<String, Object> mail = new HashMap<>();
			ArrayList<String> receivers = new ArrayList<>();
			receivers.add(to);
			
			mail.put("sender", "levlodia125@gmail.com");
			mail.put("receiver", receivers);
			mail.put("key", "xgrk qwtn jakr hqxk");
			mail.put("subject", "[ByteHub] 임시 비밀번호 안내");
			mail.put("content", String.format("안녕하세요. 요청하신 임시 비밀번호는 아래와 같습니다.\n\n임시 비밀번호 : %s\n\n※ 본 비밀번호는 30분 후 만료되며,\n  첫 로그인 후 반드시 새 비밀번호로 변경해 주세요.", tempPassword));
			
			String result = emergencySendMail(props, mail);
			log.info("임시 비밀번호 이메일 발송 결과: {}", result);
			
		} catch (Exception e) {
			log.error("임시 비밀번호 이메일 발송 실패: {}", e.getMessage(), e);
		}
	}

	
	// 근태 인증번호 발송
	public Object attMail(Properties props, Map<String, Object> mail, String tempPasswordStr) {
		
		String sender = (String) mail.get("sender");
		String key = (String) mail.get("key");
		@SuppressWarnings("unchecked")
		ArrayList<String> receivers = (ArrayList<String>) mail.get("receiver");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, key);
			}
		});

		Message msg = new MimeMessage(session);

		try {
			log.info("근태 인증번호 이메일 발송 시작 - 수신자: {}", receivers);
			
			// 메일 내용 null 체크
			String subject = (String) mail.get("subject");
			String content = (String) mail.get("content");
			
			if (subject == null || content == null) {
				log.error("메일 제목 또는 내용이 null입니다. subject: {}, content: {}", subject, content);
				return "메일 제목 또는 내용이 설정되지 않았습니다.";
			}
			
			for (String to : receivers) {
				msg.setFrom(new InternetAddress(sender));
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
				msg.setSubject(subject);
				msg.setText(content);
				
				Transport.send(msg);
				log.info("근태 인증번호 이메일 발송 완료 - 수신자: {}", to);
			}
		} catch (Exception e) {
			log.error("근태 인증번호 이메일 발송 실패: {}", e.getMessage(), e);
			e.printStackTrace();
			return "이메일 발송에 실패했습니다.(exception)";
		}

		return "근태 인증번호 이메일을 발송했습니다.";
		
	}

}
