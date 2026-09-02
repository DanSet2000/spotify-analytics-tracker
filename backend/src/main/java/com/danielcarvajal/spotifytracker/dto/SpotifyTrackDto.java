package com.danielcarvajal.spotifytracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTrackDto(
        String id,
        String name,
        @JsonProperty("duration_ms") int durationMs,
        SpotifyAlbumDto album,
        List<SpotifyArtistDto> artists) {

    public String artistNames() {
        return artists == null ? "" : artists.stream()
                .map(SpotifyArtistDto::name)
                .collect(Collectors.joining(", "));
    }
}
