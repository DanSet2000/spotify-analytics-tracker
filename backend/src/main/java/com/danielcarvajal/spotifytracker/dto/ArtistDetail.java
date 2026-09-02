package com.danielcarvajal.spotifytracker.dto;

import java.util.List;

public record ArtistDetail(
        String id,
        String name,
        long plays,
        List<AlbumStats> topAlbums,
        List<TrackStats> topTracks) {
}
