package com.bol.user.service;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.dto.response.UserDto;
import com.bol.user.model.User;
import com.bol.user.repository.UserRepository;
import com.bol.security.jwt.service.JwtService;
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

    public boolean isUserExists(UUID userId) {
        return userRepository.existsById(userId);
    }

    public UserDto registerUser(RegisterDto body) {
        var username = body.username();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        var password = passwordEncoder.encode(body.password());
        var user = userRepository.save(new User(UUID.randomUUID(), username, password));
        var token = generateToken(user);
        return toDto(user, token);
    }

    public UserDto loginUser(LoginDto body) {
        var username = body.username();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found: username=%s".formatted(username)));

        if (!passwordEncoder.matches(body.password(), user.password())) {
            throw new BadCredentialsException("Invalid password: username=%s");
        }

        var token = generateToken(user);
        return toDto(user, token);
    }

    private String generateToken(User user) {
        return jwtService.generateToken(user.id().toString());
    }

    private UserDto toDto(User user, String token) {
        return new UserDto(user.id(), user.name(), token);
    }
}
