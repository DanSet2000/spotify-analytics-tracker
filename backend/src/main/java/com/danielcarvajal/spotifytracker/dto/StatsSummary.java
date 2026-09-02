package com.danielcarvajal.spotifytracker.dto;

import java.util.List;

public record StatsSummary(long totalPlays, long totalMsPlayed, int streakDays, List<DailyPlays> last7Days) {
}
