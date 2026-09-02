package com.danielcarvajal.spotifytracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbumDto(
        String id,
        String name,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("release_date_precision") String releaseDatePrecision,
        List<SpotifyArtistDto> artists) {
}
