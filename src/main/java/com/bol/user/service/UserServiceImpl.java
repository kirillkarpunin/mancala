package com.bol.user.service;

import com.bol.user.dto.request.LoginDto;
import com.bol.user.dto.request.RegisterDto;
import com.bol.user.model.User;
import com.bol.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public boolean isUserExists(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public User registerUser(RegisterDto body) {
        var username = body.username();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        var password = passwordEncoder.encode(body.password());
        return userRepository.save(new User(username, password));
    }

    @Override
    @Transactional(readOnly = true)
    public User loginUser(LoginDto body) {
        var username = body.username();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found: username=%s".formatted(username)));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password: username=%s");
        }

        return user;
    }
}
