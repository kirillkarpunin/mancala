package com.bol.message.service;

import com.bol.message.dto.GameMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendGameStateUpdated(GameMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/game-state.%s".formatted(message.id()), message);
    }
}
