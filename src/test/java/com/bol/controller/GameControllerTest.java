package com.bol.controller;

import com.bol.dto.request.CreateGameDto;
import com.bol.dto.response.GameDto;
import com.bol.engine.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldCreateGame() {
        var url = "http://localhost:%s/v1/games".formatted(port);
        var request = new CreateGameDto(UUID.randomUUID(), 6, 4, true, true);
        var response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldJoinGame() {
        var createUrl = "http://localhost:%s/v1/games".formatted(port);
        var createRequest = new CreateGameDto(UUID.randomUUID(), 6, 4, true, true);
        var createResponse = restTemplate.postForEntity(createUrl, createRequest, GameDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var createResponseBody = createResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.status()).isEqualTo(GameStatus.WAITING_FOR_PLAYERS);

        var gameId = createResponseBody.id();

        var joinUrl = "http://localhost:%s/v1/games/%s/join".formatted(port, gameId);
        var joinRequest = new CreateGameDto(UUID.randomUUID(), 6, 4, true, true);
        var joinResponse = restTemplate.postForEntity(joinUrl, joinRequest, GameDto.class);
        assertThat(joinResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var joinResponseBody = joinResponse.getBody();
        assertThat(joinResponseBody).isNotNull();
        assertThat(joinResponseBody.status()).isEqualTo(GameStatus.ACTIVE);
    }
}