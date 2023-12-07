package com.bol.game.controller;

import com.bol.AbstractRestControllerTest;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.game.engine.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameRestControllerTest extends AbstractRestControllerTest {

    @Test
    public void shouldCreateGame() {
        var token = registerUser().token();

        var response = sendCreateGameRequest(token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldJoinGame() {
        var firstUserToken = registerUser().token();
        var createGameResponse = sendCreateGameRequest(firstUserToken);
        assertThat(createGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var createResponseBody = createGameResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.status()).isEqualTo(GameStatus.WAITING_FOR_PLAYERS);

        var gameId = createResponseBody.id();

        var secondUserToken = registerUser().token();
        var joinGameResponse = sendJoinGameRequest(secondUserToken, gameId);
        assertThat(joinGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var joinResponseBody = joinGameResponse.getBody();
        assertThat(joinResponseBody).isNotNull();
        assertThat(joinResponseBody.status()).isEqualTo(GameStatus.ACTIVE);
    }

    @Test
    public void shouldFailWhenJoinGameWithSameUser() {
        var token = registerUser().token();
        var createGameResponse = sendCreateGameRequest(token);
        assertThat(createGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var createResponseBody = createGameResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.status()).isEqualTo(GameStatus.WAITING_FOR_PLAYERS);

        var gameId = createResponseBody.id();

        var joinGameResponse = sendJoinGameRequest(token, gameId);
        assertThat(joinGameResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldFailWhenJoinActiveGame() {
        var firstUserToken = registerUser().token();
        var createGameResponse = sendCreateGameRequest(firstUserToken);
        assertThat(createGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var createResponseBody = createGameResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.status()).isEqualTo(GameStatus.WAITING_FOR_PLAYERS);

        var gameId = createResponseBody.id();

        var secondUserToken = registerUser().token();
        var joinGameResponse = sendJoinGameRequest(secondUserToken, gameId);
        assertThat(joinGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var joinResponseBody = joinGameResponse.getBody();
        assertThat(joinResponseBody).isNotNull();
        assertThat(joinResponseBody.status()).isEqualTo(GameStatus.ACTIVE);

        var thirdUserToken = registerUser().token();
        var response = sendJoinGameRequest(thirdUserToken, gameId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<GameDto> sendCreateGameRequest(String token) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token));

        var url = "http://localhost:%s/api/v1/games".formatted(port);
        var requestBody = new CreateGameDto(6, 4, true, true);
        var request = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.exchange(url, HttpMethod.POST, request, GameDto.class);
    }

    private ResponseEntity<GameDto> sendJoinGameRequest(String token, UUID gameId) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token));

        var url = "http://localhost:%s/api/v1/games/%s/join".formatted(port, gameId);
        var request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(url, HttpMethod.POST, request, GameDto.class);
    }
}
