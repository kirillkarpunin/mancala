package com.bol.user.repository;

import com.bol.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsById(UUID id);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    User save(User user);
}
