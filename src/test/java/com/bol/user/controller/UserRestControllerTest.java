package com.bol.user.controller;

import com.bol.AbstractRestControllerTest;
import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserRestControllerTest extends AbstractRestControllerTest {

    @Test
    public void shouldRegisterUser() {
        registerUser();
    }

    @ParameterizedTest(name = "should fail when register user with username [{0}]")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void shouldFailWhenRegisterUserWithInvalidUsername(String username) {
        var url = "http://localhost:%s/api/v1/auth/register".formatted(port);
        var request = new RegisterDto(username, RandomString.make(8));
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldFailWhenRegisterUserWithSameUsername() {
        var username = RandomString.make(8);
        registerUser(username, RandomString.make(8));

        var url = "http://localhost:%s/api/v1/auth/register".formatted(port);
        var request = new RegisterDto(username, RandomString.make(8));
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "should fail when register user with password [{0}]")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void shouldFailWhenRegisterUserWithInvalidPassword(String password) {
        var url = "http://localhost:%s/api/v1/auth/register".formatted(port);
        var request = new RegisterDto(RandomString.make(8), password);
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldLoginUser() {
        var username = RandomString.make(8);
        var password = RandomString.make(8);
        registerUser(username, password);

        var url = "http://localhost:%s/api/v1/auth/login".formatted(port);
        var request = new LoginDto(username, password);
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertThat(body).isNotNull();
    }

    @ParameterizedTest(name = "should fail when login user with username [{0}]")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void shouldFailWhenLoginUserWithInvalidUsername(String username) {
        var url = "http://localhost:%s/api/v1/auth/login".formatted(port);
        var request = new LoginDto(username, RandomString.make(8));
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "should fail when login user with password [{0}]")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void shouldFailWhenLoginUserWithInvalidPassword(String password) {
        var url = "http://localhost:%s/api/v1/auth/login".formatted(port);
        var request = new LoginDto(RandomString.make(8), password);
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldFailWhenLoginUserThatDoesNotExist() {
        var url = "http://localhost:%s/api/v1/auth/login".formatted(port);
        var request = new LoginDto(RandomString.make(8), RandomString.make(8));
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void shouldFailWhenLoginUserWithWrongPassword() {
        var username = RandomString.make(8);
        registerUser(username, RandomString.make(8));

        var url = "http://localhost:%s/api/v1/auth/login".formatted(port);
        var request = new LoginDto(username, RandomString.make(8));
        var response = restTemplate.postForEntity(url, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
