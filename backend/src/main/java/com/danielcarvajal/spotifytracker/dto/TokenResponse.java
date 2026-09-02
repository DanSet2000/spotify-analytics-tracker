package com.danielcarvajal.spotifytracker.dto;

import java.time.Instant;

public record TokenResponse(String token, Instant expiresAt) {
}
