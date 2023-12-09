package com.bol;

import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected UserDto registerUser() {
        return registerUser(RandomString.make(8), RandomString.make(8));
    }

    protected UserDto registerUser(String username, String password) {
        var url = "http://localhost:%s/api/v1/auth/register".formatted(port);
        var request = new RegisterDto(username, password);
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body).isNotNull();

        return body;
    }

    protected ResponseEntity<GameDto> sendCreateGameRequest(String token) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token));

        var url = "http://localhost:%s/api/v1/games".formatted(port);
        var requestBody = new CreateGameDto(6, 4, true, true);
        var request = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.exchange(url, HttpMethod.POST, request, GameDto.class);
    }

    protected ResponseEntity<GameDto> sendJoinGameRequest(String token, UUID gameId) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token));

        var url = "http://localhost:%s/api/v1/games/%s/join".formatted(port, gameId);
        var request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(url, HttpMethod.POST, request, GameDto.class);
    }
}
