package com.danielcarvajal.spotifytracker.dto;

import java.time.LocalDate;

public record DailyPlays(LocalDate date, long plays) {
}
