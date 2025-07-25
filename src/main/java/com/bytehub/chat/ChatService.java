package com.bytehub.chat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bytehub.notification.NotiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	 
    private final ChatDAO chatMapper;
    private final NotiService notiService;

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
        
        // 채팅방 정보 조회
        ChatRoomDTO room = chatMapper.selectRoomById(dto.getChat_idx());
        
        // 다른 멤버들에게 실시간 알림 전송
        for (String memberId : members) {
            if (!memberId.equals(dto.getUser_id())) {
                notiService.sendChatNotification(
                    memberId, 
                    dto.getUser_id(), 
                    room != null ? room.getChat_name() : "채팅방", 
                    dto.getContent()
                );
            }
        }
    }

    public void insertFile(ChatFileDTO dto) {
        chatMapper.insertFile(dto);
    }

    public void createRoom(ChatRoomDTO dto, List<String> userIds) {
        chatMapper.insertRoom(dto);
        for (String userId : userIds) {
            chatMapper.insertRoomMember(dto.getChat_idx(), userId);
        }
        
        // 채팅방 생성자에게 초대 알림 전송
        String creatorId = userIds.get(0); // 첫 번째 사용자를 생성자로 가정
        for (String userId : userIds) {
            if (!userId.equals(creatorId)) {
                notiService.sendChatInviteNotification(
                    userId, 
                    dto.getChat_name(), 
                    creatorId
                );
            }
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

    
    @Scheduled(cron = "0 0 3 * * ?")
    public void autoDeleteExpiredRoomsJob() {
        int deleted = chatMapper.deleteRoomsByLastActive();
        System.out.println("10일 지난 채팅방 자동 삭제: " + deleted + "건");
    }
}
