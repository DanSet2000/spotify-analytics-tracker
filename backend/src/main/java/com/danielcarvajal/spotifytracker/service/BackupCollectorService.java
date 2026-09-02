package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.config.AppProperties;
import com.danielcarvajal.spotifytracker.dto.RecentlyPlayedResponse;
import com.danielcarvajal.spotifytracker.dto.SpotifyTrackDto;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class BackupCollectorService {

    private static final Logger log = LoggerFactory.getLogger(BackupCollectorService.class);

    private final SpotifyPlayerClient player;
    private final PlayService playService;
    private final SpotifyAuthService authService;
    private final Instant ignoreBefore;

    public BackupCollectorService(SpotifyPlayerClient player,
                                  PlayService playService,
                                  SpotifyAuthService authService,
                                  AppProperties props) {
        this.player = player;
        this.playService = playService;
        this.authService = authService;
        String cutoff = props.backup() == null ? null : props.backup().ignoreBefore();
        this.ignoreBefore = cutoff == null || cutoff.isBlank() ? Instant.EPOCH : Instant.parse(cutoff);
    }

    @Scheduled(
            fixedDelayString = "${app.backup.interval-ms}",
            initialDelayString = "${app.backup.initial-delay-ms}")
    public void collect() {
        if (!authService.isConnected()) {
            return;
        }
        try {
            List<RecentlyPlayedResponse.Item> items = player.recentlyPlayed();
            int inserted = 0;
            for (RecentlyPlayedResponse.Item item : items.reversed()) {
                if (recordFromHistory(item)) {
                    inserted++;
                }
            }
            log.info("Respaldo: {} nuevas de {} escuchas recientes", inserted, items.size());
        } catch (RestClientException e) {
            log.warn("Fallo en el respaldo: {}", e.getMessage());
        }
    }

    private boolean recordFromHistory(RecentlyPlayedResponse.Item item) {
        SpotifyTrackDto track = item.track();
        if (track == null || track.id() == null || track.album() == null || item.playedAt() == null) {
            return false;
        }
        Instant endedAt = Instant.parse(item.playedAt());
        if (endedAt.isBefore(ignoreBefore)) {
            return false;
        }
        Instant startedAt = endedAt.minusMillis(track.durationMs());
        boolean saved = playService.record(track, startedAt, endedAt, track.durationMs());
        if (saved) {
            log.info("Respaldo registro: {} - {} (termino {})", track.artistNames(), track.name(), endedAt);
        }
        return saved;
    }
}
