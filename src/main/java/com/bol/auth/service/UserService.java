package com.bol.auth.service;

import com.bol.auth.dto.request.LoginDto;
import com.bol.auth.dto.request.RegisterDto;
import com.bol.auth.dto.response.UserDto;
import com.bol.auth.model.User;
import com.bol.auth.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// TODO: Interface
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserService(
            PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository userRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public UserDto registerUser(RegisterDto body) {
        var username = body.username();
        // TODO: Exception handler
        if (userRepository.findByUsername(username).isPresent()) {
            // TODO: Use different exception class ?
            throw new IllegalStateException("Username is already taken");
        }

        var password = passwordEncoder.encode(body.password());
        var user = userRepository.save(new User(UUID.randomUUID(), username, password));
        var token = jwtService.generateToken(user);
        return toDto(user, token);
    }

    public UserDto loginUser(LoginDto body) {
        var username = body.username();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found: username=%s".formatted(username)));

        if (!passwordEncoder.matches(body.password(), user.password())) {
            throw new BadCredentialsException("Invalid password: username=%s");
        }

        var token = jwtService.generateToken(user);
        return toDto(user, token);
    }

    private UserDto toDto(User user, String token) {
        return new UserDto(user.id(), user.name(), token);
    }
}
