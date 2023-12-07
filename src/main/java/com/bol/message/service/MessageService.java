package com.bol.message.service;

import com.bol.game.dto.response.GameDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// TODO: interface
@Service
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendState(GameDto message) {
        simpMessagingTemplate.convertAndSend("/topic/game-state.%s".formatted(message.id()), message);
    }
}
