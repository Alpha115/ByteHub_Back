package com.bytehub.board;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.utils.JwtUtils;

@RestController
public class BoardController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired BoardService svc;
    
    // 게시글 작성
    @PostMapping("/board/write")
    public Map<String, Object> postWrite(
    		@RequestBody BoardDTO dto, 
    		@RequestHeader Map<String,String> header) {
        
    	// 변수 초기화
    	Map<String,Object> result = new HashMap<>();
    	String loginId = null;
    	boolean login = false;
    	boolean success = false;
    	
    	loginId = (String) JwtUtils.readToken(header.get("authorization")).get("id");
    	
    	result.put("success", false);
    	result.put("loginYN", false);

    	if(loginId != null && !loginId.isEmpty()) { // loginId가 비어 있지 으면
    		login = true;
    			dto.setUser_id(loginId); // 게시글에 작성자 ID 설정
    	}
    	
    	success = svc.postWrite(dto); // 게시글 작성 서비스 호출
        result.put("idx", dto.getPost_idx()); // 작성한 게시글 idx 가져오기
        
        result.put("success", success); // 성공 여부
	    result.put("loginYN", login);
    	
    	return result;
    }
    
    // 게시글 수정
    @PutMapping("/board/update")
    public Map<String, Object> postUpdate(
    		@RequestBody BoardDTO dto, // JSON 바디 바인딩하려면 @RequestBody 필수)
    		@RequestHeader Map<String, String> header){
    	
    	Map<String, Object> result = new HashMap<>();
        String loginId = null;
        boolean login = false;
        boolean success = false;
        
	    loginId = (String) JwtUtils.readToken(header.get("authorization")).get("id");
	    
	    result.put("success", false);
		result.put("loginYN", false);
		
		
		
		if (loginId != null && !loginId.isEmpty()) {
	        login = true;
	        
	        String postWriter = svc.postWriter(dto.getPost_idx());
	        if (postWriter != null && loginId.equals(postWriter)) {
	        	success = svc.postUpdate(dto);
			}else {
				success = false;
			}
	    }

	    result.put("idx", dto.getPost_idx());
	    result.put("success", success);
	    result.put("loginYN", login);

	    return result;
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
