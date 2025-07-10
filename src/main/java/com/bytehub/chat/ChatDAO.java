package com.bytehub.chat;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatDAO {
	
	List<ChatRoomDTO> selectAllRooms();
    ChatRoomDTO selectRoomById(Integer chat_idx);

    List<String> selectRoomMembers(Integer chat_idx);
    List<ChatMessageDTO> selectMessagesByRoom(Integer chat_idx);
    List<ChatFileDTO> selectFilesByRoom(Integer chat_idx);

    void insertMessage(ChatMessageDTO dto);
    void insertFile(ChatFileDTO dto);

    void insertRoom(ChatRoomDTO dto);
    void insertRoomMember(Integer chat_idx, String user_id);
    void deleteRoomMembers(Integer chat_idx);

    void updateRoom(ChatRoomDTO dto);
    void updateRoomMembers(Integer chat_idx, List<String> user_ids);
    
}
