package com.bol.user.service;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.model.User;

import java.util.UUID;

public interface UserService {
    boolean isUserExists(UUID userId);

    User registerUser(RegisterDto body);

    User loginUser(LoginDto body);

}
