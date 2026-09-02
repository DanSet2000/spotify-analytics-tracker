package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.dto.CurrentlyPlayingResponse;
import com.danielcarvajal.spotifytracker.dto.SpotifyTrackDto;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class PlayTrackerService {

    private static final Logger log = LoggerFactory.getLogger(PlayTrackerService.class);
    private static final long RESTART_THRESHOLD_MS = 15_000;
    private static final long SEEK_TOLERANCE_MS = 5_000;

    private final SpotifyPlayerClient player;
    private final PlayService playService;
    private final SpotifyAuthService authService;

    private Session current;

    public PlayTrackerService(SpotifyPlayerClient player,
                              PlayService playService,
                              SpotifyAuthService authService) {
        this.player = player;
        this.playService = playService;
        this.authService = authService;
    }

    @Scheduled(fixedDelayString = "${app.tracker.interval-ms}")
    public void poll() {
        if (!authService.isConnected()) {
            return;
        }
        try {
            CurrentlyPlayingResponse response = player.currentlyPlaying().orElse(null);
            handle(response, Instant.now());
        } catch (HttpClientErrorException.Unauthorized e) {
            authService.invalidateAccessToken();
            log.warn("Spotify rechazo el token, se renovara en el siguiente sondeo");
        } catch (RestClientException e) {
            log.warn("Fallo consultando Spotify: {}", e.getMessage());
        }
    }

    private void handle(CurrentlyPlayingResponse response, Instant now) {
        if (response == null || !response.isTrack()) {
            finish(now);
            return;
        }

        SpotifyTrackDto track = response.item();
        long progress = response.progressMs() == null ? 0 : response.progressMs();

        boolean sameTrack = current != null && current.track.id().equals(track.id());
        boolean restarted = sameTrack
                && progress < RESTART_THRESHOLD_MS
                && progress + SEEK_TOLERANCE_MS < current.lastProgressMs;

        if (!sameTrack || restarted) {
            finish(now);
            current = new Session(track, now.minusMillis(progress), progress, now);
        } else if (response.isPlaying()) {
            long elapsed = Duration.between(current.lastPollAt, now).toMillis();
            current.msPlayed = Math.min(current.msPlayed + elapsed, track.durationMs());
        }

        current.lastProgressMs = progress;
        current.lastPollAt = now;
    }

    private void finish(Instant endedAt) {
        if (current == null) {
            return;
        }
        Session session = current;
        current = null;

        SpotifyTrackDto track = session.track;
        if (!playService.countsAsPlay(track.durationMs(), session.msPlayed)) {
            log.debug("Descartada: {} - {} ({} s)",
                    track.artistNames(), track.name(), session.msPlayed / 1000);
            return;
        }
        if (playService.record(track, session.startedAt, endedAt, session.msPlayed)) {
            log.info("Play registrado: {} - {} ({} s)",
                    track.artistNames(), track.name(), session.msPlayed / 1000);
        }
    }

    private static final class Session {
        final SpotifyTrackDto track;
        final Instant startedAt;
        long msPlayed;
        long lastProgressMs;
        Instant lastPollAt;

        Session(SpotifyTrackDto track, Instant startedAt, long initialProgressMs, Instant now) {
            this.track = track;
            this.startedAt = startedAt;
            this.msPlayed = initialProgressMs;
            this.lastProgressMs = initialProgressMs;
            this.lastPollAt = now;
        }
    }
}
