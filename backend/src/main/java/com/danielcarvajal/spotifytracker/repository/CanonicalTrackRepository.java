package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.CanonicalTrack;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanonicalTrackRepository extends JpaRepository<CanonicalTrack, UUID> {
}
