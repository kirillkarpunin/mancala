package com.bol.user.controller;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import com.bol.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @Valid
    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegisterDto body) {
        return userService.registerUser(body);
    }

    @Valid
    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginDto body) {
        return userService.loginUser(body);
    }
}