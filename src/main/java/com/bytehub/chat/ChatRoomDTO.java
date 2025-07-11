package com.bytehub.chat;

import java.util.List;

import lombok.Data;

@Data
public class ChatRoomDTO {
    private Integer chat_idx;
    private String chat_name;
    private String avatar;
    private Boolean archived;
    private int unread;
    private String last_msg;
    private String last_time;
    private String last_active;
    private String reg_date;
    private List<String> members;
    private List<ChatMessageDTO> messages;
    private List<ChatFileDTO> files;
}
