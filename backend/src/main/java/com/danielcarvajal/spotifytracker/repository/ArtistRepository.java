package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, String> {
}
