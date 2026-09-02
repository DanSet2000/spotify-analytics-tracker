package com.danielcarvajal.spotifytracker.dto;

import java.util.List;
import java.util.UUID;

public record AlbumDetail(
        UUID id,
        String name,
        String artist,
        long plays,
        List<TrackStats> topTracks,
        List<AlbumEdition> editions) {
}
