package com.bol.game.controller;

import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.game.engine.model.GameStatus;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldCreateGame() {
        var token = registerUser();

        var response = sendCreateGameRequest(token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldJoinGame() {
        var firstUserToken = registerUser();
        var createGameResponse = sendCreateGameRequest(firstUserToken);
        assertThat(createGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var createResponseBody = createGameResponse.getBody();
        assertThat(createResponseBody).isNotNull();
        assertThat(createResponseBody.status()).isEqualTo(GameStatus.WAITING_FOR_PLAYERS);

        var gameId = createResponseBody.id();

        var secondUserToken = registerUser();
        var joinGameResponse = sendJoinGameRequest(secondUserToken, gameId);
        assertThat(joinGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var joinResponseBody = joinGameResponse.getBody();
        assertThat(joinResponseBody).isNotNull();
        assertThat(joinResponseBody.status()).isEqualTo(GameStatus.ACTIVE);
    }

    // TODO: Cases
    //  - same user joins
    //  - user joins active/finished game

    private String registerUser() {
        var url = "http://localhost:%s/api/v1/auth/register".formatted(port);
        var request = new RegisterDto(RandomString.make(8), RandomString.make(8));
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body).isNotNull();

        return body.token();
    }

    private ResponseEntity<GameDto> sendCreateGameRequest(String token) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token));

        var url = "http://localhost:%s/api/v1/games".formatted(port);
        var requestBody = new CreateGameDto( 6, 4, true, true);
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