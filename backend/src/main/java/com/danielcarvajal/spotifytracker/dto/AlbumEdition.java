package com.danielcarvajal.spotifytracker.dto;

import java.time.LocalDate;

public record AlbumEdition(String id, String name, String editionLabel, LocalDate releaseDate, Long plays) {
}
