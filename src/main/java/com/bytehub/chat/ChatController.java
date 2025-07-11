package com.bytehub.chat;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {


    private final ChatService chatService;
    
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    // 전체 채팅방 목록
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getRooms(@RequestParam("user_id") String user_id) {
        return chatService.getAllRooms(user_id);
    }

    // 채팅방 상세
    @GetMapping("/room/{chat_idx}")
    public ChatRoomDTO getRoom(@PathVariable Integer chat_idx) {
        return chatService.getRoom(chat_idx);
    }

    // 채팅방 생성
    @PostMapping("/room")
    public Map<String, Object> createRoom(@RequestBody Map<String, Object> payload) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setChat_name((String) payload.get("name"));
        dto.setAvatar((String) payload.get("avatar"));
        dto.setArchived(false);
        List<String> members = (List<String>) payload.get("members");
        chatService.createRoom(dto, members);
        Map<String, Object> result = new HashMap<>();
        result.put("id", dto.getChat_idx());
        return result;
    }

    // 채팅방 멤버 수정
    @PostMapping("/room/{chat_idx}/members")
    public void updateMembers(@PathVariable Integer chat_idx, @RequestBody List<String> members) {
        chatService.updateRoomMembers(chat_idx, members);
    }

    // 메시지 전송 (WebSocket에서 주로 사용, REST도 가능)
    @PostMapping("/room/{chat_idx}/message")
    public Map<String,Object> sendMessage(@PathVariable Integer chat_idx, @RequestBody ChatMessageDTO msg) {
        msg.setChat_idx(chat_idx);
        chatService.insertMessage(msg);
        Map<String,Object> result = new HashMap<>();
        result.put("msg_idx", msg.getMsg_idx()); // insert 후 PK 반환
        return result;
    }
    
    // 채팅방 입장 시 unread 0으로 초기화
    @PostMapping("/room/{chat_idx}/reset-unread")
    public void resetUnread(@PathVariable Integer chat_idx, @RequestBody Map<String, String> payload) {
        chatService.resetUnread(chat_idx, payload.get("user_id"));
    }
    
 // 1. 실제 파일 업로드 (파일만 저장, 메타 등록은 별도)
    @PostMapping("/file/upload")
    public Map<String, Object> uploadFile(
            @RequestParam("file") MultipartFile file) throws Exception {

        // 1. 실제 파일 저장
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String saveName = uuid + ext;
        String savePath = Paths.get(uploadDir, saveName).toString();

        File dest = new File(savePath);
        file.transferTo(dest);

        // 2. 결과 반환 (메타 등록은 별도 API에서 처리)
        Map<String, Object> result = new HashMap<>();
        result.put("originalName", originalName);
        result.put("saveName", saveName);
        result.put("url", "/chat/file/download/" + saveName);
        result.put("size", file.getSize());
        return result;
    }

    // 2. 파일 메타 등록 (DB에 저장)
    @PostMapping("/file/meta")
    public Map<String, Object> saveFileMeta(@RequestBody ChatFileDTO fileDTO) {
        chatService.insertFile(fileDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("file_idx", fileDTO.getFile_idx());
        return result;
    }

    // 3. 파일 다운로드
    @GetMapping("/file/download/{saveName}")
    public void downloadFile(@PathVariable String saveName, HttpServletResponse response) throws Exception {
        File file = new File(uploadDir, saveName);
        if (!file.exists()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        String contentType = Files.probeContentType(file.toPath());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + saveName + "\"");
        try (FileInputStream fis = new FileInputStream(file)) {
            StreamUtils.copy(fis, response.getOutputStream());
        }
    }
    
    //보관
    @PostMapping("/archived")
    public Map<String, Object> archived(@RequestBody ChatRoomDTO dto){
    	
        Map<String, Object> result = new HashMap<>();
        
        boolean suc = chatService.archived(dto);
        
        result.put("suc", suc);
    	
    	return result;
    }
}
        