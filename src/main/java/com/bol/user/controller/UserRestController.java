package com.bol.user.controller;

import com.bol.user.api.UserRestApi;
import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import com.bol.user.facade.UserFacade;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class UserRestController implements UserRestApi {

    private final UserFacade userFacade;

    public UserRestController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDto register(RegisterDto body) {
        return userFacade.register(body);
    }

    @Override
    public UserDto login(LoginDto body) {
        return userFacade.login(body);
    }
}
