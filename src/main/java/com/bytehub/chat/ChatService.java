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

	    public Integer createRoom(ChatRoomDTO dto, List<String> memberIds) {
	        chatMapper.insertRoom(dto);
	        for (String userId : memberIds) {
	            ChatMemberDTO member = new ChatMemberDTO();
	            member.setChat_idx(dto.getChat_idx());
	            member.setUser_id(userId);
	            member.setUnread(0);
	            chatMapper.insertRoomMember(member);
	        }
	        return dto.getChat_idx();
	    }

	    public void updateRoomMembers(Integer chat_idx, List<String> memberIds) {
	        chatMapper.deleteRoomMembers(chat_idx);
	        for (String userId : memberIds) {
	            ChatMemberDTO member = new ChatMemberDTO();
	            member.setChat_idx(chat_idx);
	            member.setUser_id(userId);
	            member.setUnread(0);
	            chatMapper.insertRoomMember(member);
	        }
	    }

	    public void addMessage(Integer chat_idx, ChatMsgDTO msg) {
	        msg.setChat_idx(chat_idx);
	        chatMapper.insertMessage(msg);
	    }

	    public void addFile(ChatFileDTO file) {
	        chatMapper.insertFile(file);
	    }
	
}
