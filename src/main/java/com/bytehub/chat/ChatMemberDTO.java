package com.bytehub.chat;

import lombok.Data;

@Data
public class ChatMemberDTO {
    private Integer chat_idx;
    private String user_id;
    private Integer unread;
}
