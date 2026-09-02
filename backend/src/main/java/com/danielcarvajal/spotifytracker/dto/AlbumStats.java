package com.danielcarvajal.spotifytracker.dto;

import java.util.UUID;

public record AlbumStats(UUID id, String name, String artist, Long plays) {
}
