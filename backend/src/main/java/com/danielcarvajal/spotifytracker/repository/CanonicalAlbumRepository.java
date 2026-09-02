package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.Artist;
import com.danielcarvajal.spotifytracker.model.CanonicalAlbum;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanonicalAlbumRepository extends JpaRepository<CanonicalAlbum, UUID> {

    Optional<CanonicalAlbum> findByCanonicalNameIgnoreCaseAndPrimaryArtist(String canonicalName, Artist primaryArtist);
}
