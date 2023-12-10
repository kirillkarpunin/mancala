package com.bol.game.controller;

import com.bol.AbstractControllerTest;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.engine.model.GameStatus;
import com.bol.message.configuration.WebSocketConfiguration;
import com.bol.message.dto.GameMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.bol.message.configuration.WebSocketConfiguration.APP_DESTINATION_PATH_PREFIX;
import static com.bol.message.configuration.WebSocketConfiguration.WEBSOCKET_PATH;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameWebSocketControllerTest extends AbstractControllerTest {

    @Test
    public void shouldConnectToWebSocket() throws ExecutionException, InterruptedException, TimeoutException {
        // Create game
        var firstUser = registerUser();
        var firstUserToken = firstUser.token();
        var game = sendCreateGameRequest(firstUserToken).getBody();
        var gameId = game.id();

        var client = prepareWebSocketClient();

        var firstMessageQueue = new ArrayBlockingQueue<GameMessage>(1);
        var firstSession = connect(client, firstUserToken, gameId, firstMessageQueue);

        var secondUser = registerUser();
        var secondUserToken = secondUser.token();
        var secondMessageQueue = new ArrayBlockingQueue<GameMessage>(1);
        var secondSession = connect(client, secondUserToken, gameId, secondMessageQueue);
        sendJoinGameRequest(secondUserToken, gameId);

        // Join game
        Consumer<GameMessage> assertFunction = msg -> {
            assertEquals(gameId, msg.id());
            assertEquals(GameStatus.ACTIVE, msg.status());
            assertArrayEquals(new int[]{4, 4, 4, 4, 0, 4, 4, 4, 4, 0}, msg.board());
            assertEquals(0, msg.currentPlayerIndex());
        };
        var message = firstMessageQueue.take();
        assertFunction.accept(message);

        message = secondMessageQueue.take();
        assertFunction.accept(message);

        // First turn
        var sendUrl = "%s/game.%s".formatted(APP_DESTINATION_PATH_PREFIX, gameId);
        firstSession.send(sendUrl, new RequestTurnDto(2));
        assertFunction = msg -> {
            assertEquals(gameId, msg.id());
            assertEquals(GameStatus.ACTIVE, msg.status());
            assertArrayEquals(new int[]{4, 4, 0, 5, 1, 5, 5, 4, 4, 0}, msg.board());
            assertEquals(1, msg.currentPlayerIndex());
        };

        message = firstMessageQueue.take();
        assertFunction.accept(message);

        message = secondMessageQueue.take();
        assertFunction.accept(message);

        // Second turn
        secondSession.send(sendUrl, new RequestTurnDto(8));
        assertFunction = msg -> {
            assertEquals(gameId, msg.id());
            assertEquals(GameStatus.ACTIVE, msg.status());
            assertArrayEquals(new int[]{5, 5, 1, 5, 1, 5, 5, 4, 0, 1}, msg.board());
            assertEquals(0, msg.currentPlayerIndex());
        };

        message = firstMessageQueue.take();
        assertFunction.accept(message);

        message = secondMessageQueue.take();
        assertFunction.accept(message);
    }

    private static WebSocketStompClient prepareWebSocketClient() {
        var client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());

        return client;
    }

    private StompSession connect(WebSocketStompClient client, String token, UUID gameId, BlockingQueue<GameMessage> queue) throws ExecutionException, InterruptedException, TimeoutException {
        var url = "ws://localhost:%d%s".formatted(port, WEBSOCKET_PATH);

        var httpHeaders = prepareHttpHeaders(token);
        var webSocketHttpHeaders = new WebSocketHttpHeaders(httpHeaders);

        var session = client.connectAsync(url, webSocketHttpHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        var subscribeUrl = "%s/game-state.%s".formatted(WebSocketConfiguration.TOPIC_DESTINATION_PATH_PREFIX, gameId);
        session.subscribe(subscribeUrl, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.add((GameMessage) payload);
            }
        });

        return session;
    }
}
