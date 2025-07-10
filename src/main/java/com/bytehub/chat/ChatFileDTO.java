package com.bytehub.chat;

import lombok.Data;

@Data
public class ChatFileDTO {
	
    private Integer file_idx;
    private Integer msg_idx;
    private String name;
    private String url;
    private Long size;
    private String uploaded_at;
    private String expire_at;

}
