package com.bol;

import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractRestControllerTest {

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
}
