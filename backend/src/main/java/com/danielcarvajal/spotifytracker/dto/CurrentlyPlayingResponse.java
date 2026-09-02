package com.danielcarvajal.spotifytracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentlyPlayingResponse(
        @JsonProperty("is_playing") boolean isPlaying,
        @JsonProperty("progress_ms") Long progressMs,
        @JsonProperty("currently_playing_type") String currentlyPlayingType,
        SpotifyTrackDto item) {

    public boolean isTrack() {
        return "track".equals(currentlyPlayingType) && item != null && item.id() != null;
    }
}
