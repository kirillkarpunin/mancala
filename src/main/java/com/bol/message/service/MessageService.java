package com.bol.message.service;

import com.bol.game.dto.response.GameDto;

public interface MessageService {

    void sendGameStateUpdated(GameDto message);
}
