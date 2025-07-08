package com.bytehub.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@RestController
public class BoardController {


    @Autowired BoardService svc;
    
    // 게시글 작성
    @PostMapping("/board/write")
    public Map<String, Object> postWrite(
    		@RequestBody BoardDTO dto, 
    		@RequestHeader Map<String,String> header) {
    	
    	log.info("header : "+header); // 요청 헤더 로그 출력
        
    	Map<String,Object> result = new HashMap<>(); // 응답 데이터 저장용
    	
    	String loginId = null;
    	boolean login = false;
    	boolean success = false;
    	
    	// JWT 토큰에서 로그인 ID 추출 (try-catch로 JWT 오류 처리)
    	try {
    		loginId = (String) JwtUtils.readToken(header.get("authorization")).get("id");
    	} catch (Exception e) {
    		log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
    		loginId = null;
    	}
    	
    	result.put("success", false);
    	result.put("loginYN", false);

    	// 로그인 ID가 있으면 게시글 작성자로 설정
    	if(loginId != null && !loginId.isEmpty()) {
    		login = true;
    			dto.setUser_id(loginId); // 게시글에 작성자 ID 설정
    	}
    	
    	success = svc.postWrite(dto); // 게시글 작성 서비스 호출
    	
        result.put("idx", dto.getPost_idx()); // 작성한 게시글 idx 가져오기
        result.put("success", success); // 성공 여부
	    result.put("loginYN", login); // 로그인 여부
	    
    	return result;
    }
    
    // 게시글 수정
    @PutMapping("/board/update")
    public Map<String, Object> postUpdate(
    		@RequestBody BoardDTO dto, // JSON 바디 바인딩하려면 @RequestBody 필수)
    		@RequestHeader Map<String, String> header){
    	
		log.info("header : "+header); // 요청 헤더 로그 출력
    	Map<String, Object> result = new HashMap<>(); // 응답 데이터 저장용
    	
        String loginId = null;
        boolean login = false;
        boolean success = false;
        
	    // JWT 토큰에서 로그인 ID 추출 (try-catch로 JWT 오류 처리)
	    try {
	    	String token = header.get("authorization");
	    	log.info("수정 API - 받은 토큰: {}", token);
	    	Map<String, Object> tokenData = JwtUtils.readToken(token);
	    	log.info("수정 API - 토큰 파싱 결과: {}", tokenData);
	    	loginId = (String) tokenData.get("id");
	    	log.info("수정 API - 추출된 loginId: {}", loginId);
	    } catch (Exception e) {
	    	log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
	    	// JWT 토큰 파싱 실패 시 프론트엔드에서 전송한 사용자 ID 사용
	    	loginId = dto.getUser_id();
	    	log.info("JWT 파싱 실패로 인해 프론트엔드에서 전송한 사용자 ID 사용: {}", loginId);
	    }
	    result.put("success", false);
		result.put("loginYN", false);
		
		// 로그인 ID가 있으면 게시글 작성자와 일치하는지 확인
		if (loginId != null && !loginId.isEmpty()) {
	        login = true;
			String postWriter = svc.postWriter(dto.getPost_idx()); // DB에서 게시글 작성자 ID 조회
			log.info("수정 권한 확인 - loginId: {}, postWriter: {}, post_idx: {}", loginId, postWriter, dto.getPost_idx());
	        if (postWriter != null && loginId.equals(postWriter)) {
	        	success = svc.postUpdate(dto); // 게시글 수정
	        	log.info("수정 성공: {}", success);
			}else {
				success = false; // 작성자 불일치
				log.warn("수정 실패 - 작성자 불일치: loginId={}, postWriter={}", loginId, postWriter);
			}
	    }
		
	    result.put("idx", dto.getPost_idx()); // 게시글 인덱스
	    result.put("success", success); // 성공 여부
	    result.put("loginYN", login); // 로그인 여부
	    
	    return result;
    }
    
    // 게시글 삭제
    @DeleteMapping("/post/delete")
    public Map<String, Object> postDel(
    		@RequestBody BoardDTO dto,
    		@RequestHeader Map<String, String> header){
    	
		log.info("header : "+header); // 요청 헤더 로그 출력
    	Map<String, Object> result = new HashMap<>(); // 응답 데이터 저장용
    	
        boolean login = false;
        
        String token = header.get("authorization");
		String loginId = null;
		try {
			Map<String, Object> payload = JwtUtils.readToken(token);
			loginId = (String) payload.get("id");
		} catch (Exception e) {
			log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
			// JWT 토큰 파싱 실패 시 프론트엔드에서 전송한 사용자 ID 사용
			loginId = dto.getUser_id();
			log.info("JWT 파싱 실패로 인해 프론트엔드에서 전송한 사용자 ID 사용: {}", loginId);
		}
		
		// 로그인 ID가 있으면 게시글 작성자와 일치하는지 확인
		if (loginId != null && !loginId.isEmpty()) {
			
			String postWriter = svc.postWriter(dto.getPost_idx()); // DB에서 게시글 작성자 ID 조회
			log.info("삭제 권한 확인 - loginId: {}, postWriter: {}, post_idx: {}", loginId, postWriter, dto.getPost_idx());
			
			if (loginId.equals(postWriter)) {
				boolean success = svc.postDel(dto); // 게시글 삭제
				result.put("success", success); // 성공 여부
				login = true;
				log.info("삭제 성공: {}", success);
			} else {
				log.warn("삭제 실패 - 작성자 불일치: loginId={}, postWriter={}", loginId, postWriter);
			}
		}
		
		result.put("loginYN", login); // 로그인 여부
		
		return result;
    }
    
    // 게시글 리스트
    @GetMapping ("/post/list/{page}")
    public Map<String, Object> postList(
    		@PathVariable int page,
    		@RequestHeader Map<String, String> header){
    	
    	Map<String, Object> result = new HashMap<>(); // 응답 데이터 저장용

    	String loginId = null;
        boolean login = false;
        
    	// JWT 토큰에서 로그인 ID 추출 (try-catch로 JWT 오류 처리)
    	try {
    		loginId = (String) JwtUtils.readToken(header.get("authorization")).get("id");
    	} catch (Exception e) {
    		log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
    		loginId = null;
    	}
        
    	if (loginId != null && !loginId.isEmpty()) {
            login = true;
        }

        List<BoardDTO> list = svc.postList(page); // svc에서 게시글 리스트만 받아옴

        result.put("success", true);
        result.put("loginYN", login);
        result.put("loginId", loginId);
        
        result.put("list", list); // 게시글 리스트
        result.put("page", page); // 페이지 정보
		
        return result;
    }
    
    // 상단 고정 게시글 개수 조회
    @GetMapping("/post/pinnedCnt")
    public int getPinnedCnt() {
        return svc.cntPinned();
    }
    
    // 게시글 상세보기
    @GetMapping("/post/detail/{post_idx}")
    public Map<String, Object> postDetail(
    		@PathVariable int post_idx,
    		@RequestHeader Map<String, String> header){
    	
    	Map<String, Object> result = new HashMap<>(); // 응답 데이터 저장용
    	
    	String loginId = null;
        boolean login = false;
        
	     // JWT 토큰에서 로그인 ID 추출 (try-catch로 JWT 오류 처리)
		try {
			loginId = (String) JwtUtils.readToken(header.get("authorization")).get("id");
		} catch (Exception e) {
			log.warn("JWT 토큰 파싱 실패: " + e.getMessage());
			loginId = null;
		}
	    
		if (loginId != null && !loginId.isEmpty()) {
	        login = true;
	    }
		
		// 게시글 상세 정보 가져오기
		BoardDTO postData = svc.postDetail(post_idx);
		
		if (postData != null) {
			// BoardDTO의 필드들을 Map에 추가
			result.put("post_idx", postData.getPost_idx());
			result.put("user_id", postData.getUser_id());
			result.put("subject", postData.getSubject());
			result.put("content", postData.getContent());
			result.put("pinned", postData.isPinned());
			result.put("draft", postData.isDraft());
			result.put("reg_date", postData.getReg_date());
			result.put("file_idx", postData.getFile_idx());
			result.put("category", postData.getCategory());
			
			// 로그인 정보 추가
			result.put("success", true);
			result.put("loginYN", login);
			result.put("loginId", loginId);
		} else {
			result.put("success", false);
			result.put("message", "게시글을 찾을 수 없습니다.");
		}
		
	    return result;
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
