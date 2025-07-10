package com.bytehub.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/chat")
public class ChatController {


    private final ChatService chatService;

    // 채팅방 전체 목록
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getRooms() {
        return chatService.getAllRooms();
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
        // lastMsg, lastTime, lastActive 등은 필요시 추가
        List<String> members = (List<String>) payload.get("members");
        Integer id = chatService.createRoom(dto, members);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        return result;
    }

    // 채팅방 멤버 수정
    @PostMapping("/room/{chat_idx}/members")
    public void updateMembers(@PathVariable Integer chat_idx, @RequestBody List<String> members) {
        chatService.updateRoomMembers(chat_idx, members);
    }

    // 메시지 전송
    @PostMapping("/room/{chat_idx}/message")
    public void sendMessage(@PathVariable Integer chat_idx, @RequestBody ChatMsgDTO msg) {
        chatService.addMessage(chat_idx, msg);
    }

    // 파일 업로드 메타 저장 (실제 파일 업로드는 별도 API 필요)
    @PostMapping("/room/{chat_idx}/file")
    public void uploadFile(@PathVariable Integer chat_idx, @RequestBody ChatFileDTO file) {
        chatService.addFile(file);
    }
	
}
