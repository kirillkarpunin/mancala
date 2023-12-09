package com.bol.user.service;

import com.bol.exception.ApplicationException;
import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.model.User;
import com.bol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public boolean isUserExists(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public User registerUser(RegisterDto body) {
        var username = body.username();
        if (userRepository.existsByUsername(username)) {
            throw ApplicationException.badRequest("Username is already taken");
        }

        var password = passwordEncoder.encode(body.password());
        return userRepository.save(new User(username, password));
    }

    @Override
    @Transactional(readOnly = true)
    public User loginUser(LoginDto body) {
        var username = body.username();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> ApplicationException.badRequest("User is not found: username=%s".formatted(username)));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw ApplicationException.badRequest("Invalid password: username=%s".formatted(username));
        }

        return user;
    }
}
