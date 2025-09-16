package com.game.project.auth.repositories;

import com.game.project.auth.models.RefreshToken;
import com.game.project.auth.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
