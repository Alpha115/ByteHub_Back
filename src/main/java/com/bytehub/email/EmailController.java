package com.bytehub.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.member.MemberService;
import com.bytehub.member.MemberDTO;

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
		private final MemberService memberService;
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
	
	// ---------비밀번호 재설정 서비스---------
	@Service
	@RequiredArgsConstructor
	public class PasswordResetService {
		
		        // 여기서부터
		private final MemberService memberService;
		private final EmailService emailService;
		
		@Transactional
		public Map<String, Object> createAndSendTempPassword(String userId, String email) {
			Map<String, Object> result = new HashMap<>();
			
			try {
				// 사용자 확인
				boolean userExists = memberService.checkUserByIdAndEmail(userId, email);
				
				if (!userExists) {
					result.put("success", false);
					result.put("message", "해당 아이디와 이메일로 등록된 사용자가 없습니다.");
					return result;
				}
				
				// 임시 비밀번호 생성
				int tempPassword = createPw();
				String tempPasswordStr = String.valueOf(tempPassword);
				
				// 비밀번호 업데이트
				boolean updateSuccess = memberService.updatePassword(userId, tempPasswordStr);
				
				if (!updateSuccess) {
					result.put("success", false);
					result.put("message", "비밀번호 업데이트에 실패했습니다.");
					return result;
				}
				
				// 이메일 발송 (비동기)
				emailService.sendTempPasswordAsync(email, tempPasswordStr);
				
				result.put("success", true);
				result.put("message", "임시 비밀번호가 이메일로 발송되었습니다.");
				
			} catch (Exception e) {
				log.error("비밀번호 재설정 중 오류 발생: {}", e.getMessage(), e);
				result.put("success", false);
				result.put("message", "비밀번호 재설정 중 오류가 발생했습니다.");
				result.put("error", e.getMessage());
			}
			
			return result;
		}
	}
	
	// ---------비밀번호 찾기 API---------
	@PostMapping("/find-password")
	public Map<String, Object> findPassword(@RequestBody Map<String, String> info) {
		log.info("비밀번호 찾기 요청 - 아이디: {}, 이메일: {}", info.get("user_id"), info.get("email"));
		
		PasswordResetService passwordResetService = new PasswordResetService(memberService, service);
		return passwordResetService.createAndSendTempPassword(info.get("user_id"), info.get("email"));
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

		resp.put("msg", service.emergencySendMail(props, mail)); // 반환값: 이메일 발송 메시지를 반환합니다. ({"msg":"이메일 발송에 성공했습니다."})

		return resp;
	}


	

}
