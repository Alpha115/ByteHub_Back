package com.bytehub.notification;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class NotiDTO {
    private String notification_id;
    private String user_id;           // 수신자
    private String type;             // 알림 타입
    private String title;            // 알림 제목
    private String content;          // 알림 내용
    private String target_url;        // 클릭 시 이동할 URL
    private boolean isRead;          // 읽음 여부
    private LocalDateTime createdAt; // 생성 시간
    
    public NotiDTO() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    public NotiDTO(String type, String title, String content) {
        this();
        this.type = type;
        this.title = title;
        this.content = content;
    }
    
    public NotiDTO(String user_id, String type, String title, String content) {
        this(type, title, content);
        this.user_id = user_id;
    }
} 