package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.CanonicalAlbum;
import com.danielcarvajal.spotifytracker.model.CanonicalTrack;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanonicalTrackRepository extends JpaRepository<CanonicalTrack, UUID> {

    Optional<CanonicalTrack> findByCanonicalNameIgnoreCaseAndCanonicalAlbum(String canonicalName, CanonicalAlbum canonicalAlbum);
}
