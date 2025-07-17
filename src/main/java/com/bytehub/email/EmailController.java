package com.bytehub.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.member.MemberService;

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
	String sender = "levlodia125@gmail.com";
	String key = "xgrk qwtn jakr hqxk";
	String enable = "true";
	Properties props = new Properties();
	
	
	// 부서정보불러오기
	@GetMapping("/depts")
	public Map<String, Object> depts(){
		resp=new HashMap<String, Object>();
		resp.put("list", memberService.depts());
		return resp;
	}
	
	
	// --------임시 비밀번호(6자리) 랜덤 발급 함수---------
	int createPw() {
		randomNum = (int) ((Math.random() * 90000) + 100000);
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
	
	

	// 근태 인증번호 발송 OR 재발송
	@PostMapping("/attendance")
	public Map<String, Object> attMail(@RequestBody Map<String, Object> info) {
		resp = new HashMap<String, Object>();
		Map<String, Object> mail = new HashMap<String, Object>();
		
		// user_id로 member 테이블에서 이메일 조회
	    String userId = (String) info.get("user_id");
	    
	    // MemberService에서 이메일 조회
	    String userEmail = memberService.findEmail(userId);
	    
	    if (userEmail == null) {
	        resp.put("msg", "사용자를 찾을 수 없습니다.");
	        return resp;
	    }

		// 6자리 인증번호 생성
		int tempPassword = createPw();
		String tempPasswordStr = String.valueOf(tempPassword);

		// 메일 제목과 내용 설정
		String subject = "[ByteHub] 근태 인증번호 안내";
		String content = String.format("안녕하세요.\n\n근태 인증번호는 아래와 같습니다.\n\n인증번호: %s\n\n※ 본 인증번호는 10분 후 만료됩니다.", tempPasswordStr);

		// 이메일 리스트 생성 (DB에서 가져온 이메일만 사용)
		ArrayList<String> receivers = new ArrayList<>();
		receivers.add(userEmail);

		mail.put("sender", sender);
		mail.put("receiver", receivers); // DB에서 가져온 이메일 사용
		mail.put("key", key);
		mail.put("subject", subject);
		mail.put("content", content);

		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", port);
		props.setProperty("mail.smtp.auth", enable);
		props.setProperty("mail.smtp.starttls.enable", enable);
		props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // <Tlsv1.2 버전 추가

		resp.put("msg", service.attMail(props, mail, tempPasswordStr)); // 반환값: 이메일 발송 메시지를 반환합니다. ({"msg":"이메일 발송에 성공했습니다."})
		resp.put("authCode", tempPasswordStr); // 얘를 프론트에서 저장해서 인증번호 확인 서비스에 전달

		return resp;
	}

	// 인증번호 확인 서비스
	@PostMapping("/verify")
	public Map<String, Object> verify(@RequestBody Map<String, String> request) {
		Map<String, Object> result = new HashMap<>();
		
		// 클라이언트에서 전송한 인증번호와 예상 인증번호 추출
		String inputCode = request.get("authCode");        // 사용자가 입력한 인증번호
		String expectedCode = request.get("expectedCode"); // 서버에서 생성한 인증번호
		
		// 인증번호가 null인지 확인
		if (inputCode == null || expectedCode == null) {
			result.put("success", false);
			result.put("msg", "인증번호를 입력해주세요.");
			return result;
		}
		
		// 인증번호 일치 여부 확인
		boolean success = inputCode.equals(expectedCode);
		
		// 결과 반환
		result.put("success", success);
		result.put("msg", success ? "인증번호가 일치합니다." : "인증번호가 일치하지 않습니다.");
		
		return result;
	}

}
