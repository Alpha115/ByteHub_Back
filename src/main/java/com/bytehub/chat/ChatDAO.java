package com.bytehub.chat;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatDAO {
	
	List<ChatRoomDTO> selectAllRooms();
    ChatRoomDTO selectRoomById(Integer chat_idx);
    void insertRoom(ChatRoomDTO dto);

    void insertRoomMember(ChatMemberDTO dto);
    void deleteRoomMembers(Integer chat_idx);
    List<String> selectRoomMembers(Integer chat_idx);

    List<ChatMsgDTO> selectMessagesByRoom(Integer chat_idx);
    void insertMessage(ChatMsgDTO dto);

    List<ChatFileDTO> selectFilesByRoom(Integer chat_idx);
    void insertFile(ChatFileDTO dto);

}
