package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.SpotifyAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyAuthRepository extends JpaRepository<SpotifyAuth, Integer> {

    default Optional<SpotifyAuth> findCurrent() {
        return findById(SpotifyAuth.SINGLE_ROW_ID);
    }

    default boolean hasCurrent() {
        return existsById(SpotifyAuth.SINGLE_ROW_ID);
    }
}
