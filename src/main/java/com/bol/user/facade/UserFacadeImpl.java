package com.bol.user.facade;

import com.bol.security.jwt.service.JwtService;
import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import com.bol.user.model.User;
import com.bol.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;

    private final JwtService jwtService;

    @Override
    public UserDto register(RegisterDto body) {
        var user = userService.registerUser(body);
        var token = generateToken(user);
        return toDto(user, token);
    }

    @Override
    public UserDto login(LoginDto body) {
        var user = userService.loginUser(body);
        var token = generateToken(user);
        return toDto(user, token);
    }

    private String generateToken(User user) {
        return jwtService.generateToken(user.getId().toString());
    }

    private UserDto toDto(User user, String token) {
        return new UserDto(user.getId(), user.getUsername(), token);
    }

}
