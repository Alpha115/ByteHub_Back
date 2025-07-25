package com.bytehub.chat;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.bytehub.notification.NotiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final NotiService notiService;

    @MessageMapping("/chat/{chat_idx}")
    @SendTo("/topic/chat/{chat_idx}")
    public ChatMessageDTO sendMessage(@DestinationVariable Integer chat_idx, ChatMessageDTO message) {
        message.setChat_idx(chat_idx);
        chatService.insertMessage(message);
        return message;
    }
}