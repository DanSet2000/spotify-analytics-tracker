package com.danielcarvajal.spotifytracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyArtistDto(String id, String name) {
}
