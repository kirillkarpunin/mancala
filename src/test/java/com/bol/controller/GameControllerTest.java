package com.bol.controller;

import com.bol.dto.request.CreateGameDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

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
        var request = new CreateGameDto(6, 4, true, true);
        var response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}