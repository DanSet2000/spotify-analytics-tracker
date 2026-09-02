package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.CanonicalAlbum;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanonicalAlbumRepository extends JpaRepository<CanonicalAlbum, UUID> {
}
