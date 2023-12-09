package com.bol.game.controller;

import com.bol.AbstractControllerTest;
import com.bol.game.engine.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GameRestControllerTest extends AbstractControllerTest {

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
}
