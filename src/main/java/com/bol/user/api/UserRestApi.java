package com.bol.user.api;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Validated
@RequestMapping("/api/v1/auth")
public interface UserRestApi {

    @Valid
    @PostMapping("/register")
    UserDto register(@Valid @RequestBody RegisterDto body);

    @Valid
    @PostMapping("/login")
    UserDto login(@Valid @RequestBody LoginDto body);
}
