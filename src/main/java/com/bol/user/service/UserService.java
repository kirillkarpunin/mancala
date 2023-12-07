package com.bol.user.service;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;

import java.util.UUID;

public interface UserService {
    boolean isUserExists(UUID userId);

    UserDto registerUser(RegisterDto body);

    UserDto loginUser(LoginDto body);

}
