package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.SpotifyAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyAuthRepository extends JpaRepository<SpotifyAuth, Integer> {
}
