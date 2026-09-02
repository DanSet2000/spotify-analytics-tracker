package com.danielcarvajal.spotifytracker.dto;

import java.time.Instant;

public record SpotifyStatus(boolean connected, Instant connectedAt) {
}
