package com.danielcarvajal.spotifytracker.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.danielcarvajal.spotifytracker.dto.CurrentlyPlayingResponse;
import com.danielcarvajal.spotifytracker.dto.SpotifyAlbumDto;
import com.danielcarvajal.spotifytracker.dto.SpotifyArtistDto;
import com.danielcarvajal.spotifytracker.dto.SpotifyTrackDto;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayTrackerServiceTest {

    private static final Instant T0 = Instant.parse("2026-09-01T00:00:00Z");
    private static final SpotifyArtistDto ARTIST = new SpotifyArtistDto("ar1", "Artist");
    private static final SpotifyTrackDto SONG_A = track("a", "Song A", 240_000);
    private static final SpotifyTrackDto SONG_B = track("b", "Song B", 200_000);

    private PlayService playService;
    private PlayTrackerService tracker;

    @BeforeEach
    void setUp() {
        playService = mock(PlayService.class);
        when(playService.record(any(), any(), any(), anyLong())).thenReturn(true);
        tracker = new PlayTrackerService(
                mock(SpotifyPlayerClient.class),
                playService,
                new ListeningRule(),
                mock(SpotifyAuthService.class));
    }

    @Test
    void recordsPlayWhenTrackChangesAfterEnoughListening() {
        tracker.handle(playing(SONG_A, 0), T0);
        tracker.handle(playing(SONG_A, 100_000), T0.plusSeconds(100));
        tracker.handle(playing(SONG_B, 0), T0.plusSeconds(120));

        verify(playService).record(SONG_A, T0, T0.plusSeconds(120), 100_000);
    }

    @Test
    void discardsTrackSkippedTooEarly() {
        tracker.handle(playing(SONG_A, 0), T0);
        tracker.handle(playing(SONG_A, 60_000), T0.plusSeconds(60));
        tracker.handle(playing(SONG_B, 0), T0.plusSeconds(80));

        verifyNoInteractions(playService);
    }

    @Test
    void pausedTimeDoesNotCount() {
        tracker.handle(playing(SONG_A, 0), T0);
        tracker.handle(playing(SONG_A, 100_000), T0.plusSeconds(100));
        tracker.handle(paused(SONG_A, 100_000), T0.plusSeconds(200));
        tracker.handle(playing(SONG_B, 0), T0.plusSeconds(220));

        verify(playService).record(SONG_A, T0, T0.plusSeconds(220), 100_000);
    }

    @Test
    void restartingTheSameTrackCountsAsNewPlay() {
        tracker.handle(playing(SONG_A, 0), T0);
        tracker.handle(playing(SONG_A, 100_000), T0.plusSeconds(100));
        tracker.handle(playing(SONG_A, 5_000), T0.plusSeconds(120));
        tracker.handle(playing(SONG_A, 105_000), T0.plusSeconds(220));
        tracker.handle(playing(SONG_B, 0), T0.plusSeconds(240));

        verify(playService).record(SONG_A, T0, T0.plusSeconds(120), 100_000);
        verify(playService).record(SONG_A, T0.plusSeconds(115), T0.plusSeconds(240), 105_000);
    }

    @Test
    void stoppingPlaybackClosesTheSession() {
        tracker.handle(playing(SONG_A, 0), T0);
        tracker.handle(playing(SONG_A, 100_000), T0.plusSeconds(100));
        tracker.handle(null, T0.plusSeconds(120));

        verify(playService).record(SONG_A, T0, T0.plusSeconds(120), 100_000);
    }

    @Test
    void creditsProgressAlreadyPlayedWhenFirstSeen() {
        tracker.handle(playing(SONG_A, 95_000), T0);
        tracker.handle(playing(SONG_B, 0), T0.plusSeconds(20));

        verify(playService).record(SONG_A, T0.minusMillis(95_000), T0.plusSeconds(20), 95_000);
    }

    @Test
    void ignoresPodcastEpisodes() {
        tracker.handle(new CurrentlyPlayingResponse(true, 200_000L, "episode", null), T0);
        tracker.handle(null, T0.plusSeconds(20));

        verifyNoInteractions(playService);
    }

    private static SpotifyTrackDto track(String id, String name, int durationMs) {
        SpotifyAlbumDto album = new SpotifyAlbumDto("al-" + id, "Album", "2020-01-01", "day", List.of(ARTIST));
        return new SpotifyTrackDto(id, name, durationMs, album, List.of(ARTIST));
    }

    private static CurrentlyPlayingResponse playing(SpotifyTrackDto track, long progressMs) {
        return new CurrentlyPlayingResponse(true, progressMs, "track", track);
    }

    private static CurrentlyPlayingResponse paused(SpotifyTrackDto track, long progressMs) {
        return new CurrentlyPlayingResponse(false, progressMs, "track", track);
    }
}
