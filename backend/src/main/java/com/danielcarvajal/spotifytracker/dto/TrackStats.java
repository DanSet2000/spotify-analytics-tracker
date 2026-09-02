package com.danielcarvajal.spotifytracker.dto;

import java.util.UUID;

public record TrackStats(UUID id, String name, String album, String artist, Long plays) {
}
