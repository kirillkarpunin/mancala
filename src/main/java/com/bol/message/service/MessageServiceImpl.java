package com.bol.message.service;

import com.bol.message.configuration.WebSocketConfiguration;
import com.bol.message.dto.GameMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendGameStateUpdated(GameMessage message) {
        var url = "%s/game-state.%s".formatted(WebSocketConfiguration.TOPIC_DESTINATION_PATH_PREFIX, message.id());
        simpMessagingTemplate.convertAndSend(url, message);
    }
}
