package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.dto.SpotifyTrackDto;
import com.danielcarvajal.spotifytracker.model.Play;
import com.danielcarvajal.spotifytracker.model.Track;
import com.danielcarvajal.spotifytracker.repository.PlayRepository;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayService {

    private static final long MIN_MS_PLAYED = 90_000;
    private static final double SHORT_TRACK_COMPLETION = 0.9;
    private static final Duration DEDUPE_WINDOW = Duration.ofSeconds(30);

    private final CatalogService catalog;
    private final PlayRepository playRepo;

    public PlayService(CatalogService catalog, PlayRepository playRepo) {
        this.catalog = catalog;
        this.playRepo = playRepo;
    }

    public boolean countsAsPlay(int durationMs, long msPlayed) {
        if (durationMs <= MIN_MS_PLAYED) {
            return msPlayed >= durationMs * SHORT_TRACK_COMPLETION;
        }
        return msPlayed > MIN_MS_PLAYED;
    }

    @Transactional
    public boolean record(SpotifyTrackDto dto, Instant startedAt, Instant endedAt, long msPlayed) {
        Track track = catalog.upsertTrack(dto);
        boolean alreadyRecorded = playRepo.existsByTrackAndEndedAtBetween(
                track, endedAt.minus(DEDUPE_WINDOW), endedAt.plus(DEDUPE_WINDOW));
        if (alreadyRecorded) {
            return false;
        }
        Play play = new Play();
        play.setTrack(track);
        play.setPlayedAt(startedAt);
        play.setEndedAt(endedAt);
        play.setMsPlayed((int) Math.min(msPlayed, Integer.MAX_VALUE));
        playRepo.save(play);
        return true;
    }
}
