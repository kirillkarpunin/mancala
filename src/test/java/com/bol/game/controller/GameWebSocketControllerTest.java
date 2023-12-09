package com.bol.game.controller;

import com.bol.AbstractControllerTest;
import com.bol.game.engine.model.GameStatus;
import com.bol.message.configuration.WebSocketConfiguration;
import com.bol.message.dto.GameMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameWebSocketControllerTest extends AbstractControllerTest {

    @Test
    public void shouldConnectToWebSocket() throws ExecutionException, InterruptedException, TimeoutException {
        var firstUser = registerUser();
        var game = sendCreateGameRequest(firstUser.token()).getBody();
        var gameId = game.id();

        var client = prepareWebSocketClient();
        var session = connect(client);

        var messageQueue = new ArrayBlockingQueue<GameMessage>(1);
        var subscribeUrl = "%s/game-state.%s".formatted(WebSocketConfiguration.TOPIC_DESTINATION_PATH_PREFIX, gameId);
        session.subscribe(subscribeUrl, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messageQueue.add((GameMessage) payload);
            }
        });

        var secondUser = registerUser();
        sendJoinGameRequest(secondUser.token(), gameId);

        Awaitility.await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> {
                    var gameMessage = messageQueue.take();
                    assertEquals(gameId, gameMessage.id());
                    assertEquals(GameStatus.ACTIVE, gameMessage.status());
                });
    }

    private static WebSocketStompClient prepareWebSocketClient() {
        var client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());

        return client;
    }

    private StompSession connect(WebSocketStompClient client) throws ExecutionException, InterruptedException, TimeoutException {
        var url = "ws://localhost:%d/websocket".formatted(port);
        return client.connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
    }

}