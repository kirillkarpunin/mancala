package com.bol.user.facade;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;

public interface UserFacade {

    UserDto register(RegisterDto body);

    UserDto login(LoginDto body);
}
