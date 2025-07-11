package com.bytehub.chat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	 
    private final ChatDAO chatMapper;

    public List<ChatRoomDTO> getAllRooms(String user_id) {
        List<ChatRoomDTO> rooms = chatMapper.selectAllRooms(user_id);
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
        // 나를 제외한 멤버의 unread +1
        List<String> members = chatMapper.selectRoomMembers(dto.getChat_idx());
        chatMapper.increaseUnreadExceptSender(dto.getChat_idx(), dto.getUser_id());
        chatMapper.updateLastActive(dto.getChat_idx(), LocalDateTime.now());
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

    public void resetUnread(Integer chat_idx, String user_id) {
        chatMapper.resetUnread(chat_idx, user_id);
    }

	public boolean archived(ChatRoomDTO dto) {
		
		int row = chatMapper.archived(dto);
		
		return row > 0 ? true : false;
	}
}
