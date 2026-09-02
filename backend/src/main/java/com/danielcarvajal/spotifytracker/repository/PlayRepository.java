package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.Play;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayRepository extends JpaRepository<Play, UUID> {
}
