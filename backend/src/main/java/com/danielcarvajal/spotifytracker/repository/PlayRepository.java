package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.Play;
import com.danielcarvajal.spotifytracker.model.Track;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayRepository extends JpaRepository<Play, UUID> {

    boolean existsByTrackAndEndedAtBetween(Track track, Instant from, Instant to);
}
