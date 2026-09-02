package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, String> {
}
