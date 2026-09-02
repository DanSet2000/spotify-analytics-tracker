package com.danielcarvajal.spotifytracker.controller;

import com.danielcarvajal.spotifytracker.dto.AlbumDetail;
import com.danielcarvajal.spotifytracker.dto.AlbumStats;
import com.danielcarvajal.spotifytracker.dto.ArtistDetail;
import com.danielcarvajal.spotifytracker.dto.ArtistStats;
import com.danielcarvajal.spotifytracker.dto.StatsSummary;
import com.danielcarvajal.spotifytracker.dto.TrackStats;
import com.danielcarvajal.spotifytracker.service.StatsService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private static final int MAX_LIMIT = 100;

    private final StatsService stats;

    public StatsController(StatsService stats) {
        this.stats = stats;
    }

    @GetMapping("/top-artists")
    public List<ArtistStats> topArtists(@RequestParam(defaultValue = "10") int limit) {
        return stats.topArtists(clamp(limit));
    }

    @GetMapping("/top-albums")
    public List<AlbumStats> topAlbums(@RequestParam(defaultValue = "10") int limit) {
        return stats.topAlbums(clamp(limit));
    }

    @GetMapping("/top-tracks")
    public List<TrackStats> topTracks(@RequestParam(defaultValue = "10") int limit) {
        return stats.topTracks(clamp(limit));
    }

    @GetMapping("/artists/{id}")
    public ArtistDetail artist(@PathVariable String id, @RequestParam(defaultValue = "10") int limit) {
        return stats.artistDetail(id, clamp(limit))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artista no encontrado"));
    }

    @GetMapping("/albums/{id}")
    public AlbumDetail album(@PathVariable UUID id, @RequestParam(defaultValue = "10") int limit) {
        return stats.albumDetail(id, clamp(limit))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album no encontrado"));
    }

    @GetMapping("/summary")
    public StatsSummary summary() {
        return stats.summary();
    }

    private static int clamp(int limit) {
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }
}
