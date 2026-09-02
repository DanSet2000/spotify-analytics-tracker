package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, String> {
}
