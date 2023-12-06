package com.bol.auth.controller;

import com.bol.auth.dto.request.LoginDto;
import com.bol.auth.dto.request.RegisterDto;
import com.bol.auth.dto.response.UserDto;
import com.bol.auth.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Validate DTOs
@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final UserService userService;

    public AuthRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterDto body) {
        return userService.registerUser(body);
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody LoginDto body) {
        return userService.loginUser(body);
    }
}