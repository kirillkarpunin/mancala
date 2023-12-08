package com.bol.message.service;

import com.bol.message.dto.GameMessage;

public interface MessageService {

    void sendGameStateUpdated(GameMessage message);
}
