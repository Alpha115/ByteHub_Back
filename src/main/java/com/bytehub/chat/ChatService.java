package com.bytehub.chat;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	 
	    private final ChatDAO chatMapper;

	    public List<ChatRoomDTO> getAllRooms() {
	        List<ChatRoomDTO> rooms = chatMapper.selectAllRooms();
	        for (ChatRoomDTO room : rooms) {
	            room.setMembers(chatMapper.selectRoomMembers(room.getChat_idx()));
	            room.setMessages(chatMapper.selectMessagesByRoom(room.getChat_idx()));
	            room.setFiles(chatMapper.selectFilesByRoom(room.getChat_idx()));
	        }
	        return rooms;
	    }

	    public ChatRoomDTO getRoom(Integer chat_idx) {
	        ChatRoomDTO room = chatMapper.selectRoomById(chat_idx);
	        if (room != null) {
	            room.setMembers(chatMapper.selectRoomMembers(chat_idx));
	            room.setMessages(chatMapper.selectMessagesByRoom(chat_idx));
	            room.setFiles(chatMapper.selectFilesByRoom(chat_idx));
	        }
	        return room;
	    }

	    public void insertMessage(ChatMessageDTO dto) {
	        chatMapper.insertMessage(dto);
	    }

	    public void insertFile(ChatFileDTO dto) {
	        chatMapper.insertFile(dto);
	    }

	    public void createRoom(ChatRoomDTO dto, List<String> userIds) {
	        chatMapper.insertRoom(dto);
	        for (String userId : userIds) {
	            chatMapper.insertRoomMember(dto.getChat_idx(), userId);
	        }
	    }

	    public void updateRoomMembers(Integer chat_idx, List<String> userIds) {
	        chatMapper.deleteRoomMembers(chat_idx);
	        for (String userId : userIds) {
	            chatMapper.insertRoomMember(chat_idx, userId);
	        }
	    }
	
}
