package com.bytehub.email;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	// 비상연락망단체메일보내기
	public String emergencySendMail(Properties props, Map<String, Object> mail) {

		// 여기서부터
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
			for (String to : receivers) {	// for문 빼고
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

}
