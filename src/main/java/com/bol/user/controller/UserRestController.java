package com.bol.user.controller;

import com.bol.user.api.UserRestApi;
import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import com.bol.user.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class UserRestController implements UserRestApi {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDto register(RegisterDto body) {
        return userService.registerUser(body);
    }

    @Override
    public UserDto login(LoginDto body) {
        return userService.loginUser(body);
    }
}
