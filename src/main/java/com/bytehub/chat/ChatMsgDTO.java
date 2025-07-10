package com.bytehub.chat;

import java.util.List;

import lombok.Data;

@Data
public class ChatMsgDTO {
    private Integer msg_idx;
    private Integer chat_idx;
    private String user_id;
    private String content;
    private String msg_type;
    private String reg_date;
    private Boolean is_read;
    private List<ChatFileDTO> files;
}
