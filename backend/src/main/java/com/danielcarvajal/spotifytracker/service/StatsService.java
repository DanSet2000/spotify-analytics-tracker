package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.config.AppProperties;
import com.danielcarvajal.spotifytracker.dto.AlbumDetail;
import com.danielcarvajal.spotifytracker.dto.AlbumStats;
import com.danielcarvajal.spotifytracker.dto.ArtistDetail;
import com.danielcarvajal.spotifytracker.dto.ArtistStats;
import com.danielcarvajal.spotifytracker.dto.DailyPlays;
import com.danielcarvajal.spotifytracker.dto.StatsSummary;
import com.danielcarvajal.spotifytracker.dto.TrackStats;
import com.danielcarvajal.spotifytracker.repository.ArtistRepository;
import com.danielcarvajal.spotifytracker.repository.CanonicalAlbumRepository;
import com.danielcarvajal.spotifytracker.repository.PlayRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private static final int LAST_DAYS = 7;

    private final PlayRepository playRepo;
    private final ArtistRepository artistRepo;
    private final CanonicalAlbumRepository canonicalAlbumRepo;
    private final ZoneId zone;

    public StatsService(PlayRepository playRepo,
                        ArtistRepository artistRepo,
                        CanonicalAlbumRepository canonicalAlbumRepo,
                        AppProperties appProperties) {
        this.playRepo = playRepo;
        this.artistRepo = artistRepo;
        this.canonicalAlbumRepo = canonicalAlbumRepo;
        this.zone = appProperties.timezone();
    }

    public List<ArtistStats> topArtists(int limit) {
        return playRepo.topArtists(Limit.of(limit));
    }

    public List<AlbumStats> topAlbums(int limit) {
        return playRepo.topAlbums(Limit.of(limit));
    }

    public List<TrackStats> topTracks(int limit) {
        return playRepo.topTracks(Limit.of(limit));
    }

    public Optional<ArtistDetail> artistDetail(String artistId, int limit) {
        return artistRepo.findById(artistId).map(artist -> new ArtistDetail(
                artist.getId(),
                artist.getName(),
                playRepo.countByArtist(artistId),
                playRepo.topAlbumsByArtist(artistId, Limit.of(limit)),
                playRepo.topTracksByArtist(artistId, Limit.of(limit))));
    }

    public Optional<AlbumDetail> albumDetail(UUID canonicalAlbumId, int limit) {
        return canonicalAlbumRepo.findById(canonicalAlbumId).map(album -> new AlbumDetail(
                album.getId(),
                album.getCanonicalName(),
                album.getPrimaryArtist().getName(),
                playRepo.countByCanonicalAlbum(canonicalAlbumId),
                playRepo.topTracksByCanonicalAlbum(canonicalAlbumId, Limit.of(limit)),
                playRepo.editionsOf(canonicalAlbumId)));
    }

    public StatsSummary summary() {
        LocalDate today = LocalDate.now(zone);
        return new StatsSummary(
                playRepo.count(),
                playRepo.totalMsPlayed(),
                streakDays(today),
                last7Days(today));
    }

    private int streakDays(LocalDate today) {
        List<String> days = playRepo.distinctPlayDays(zone.getId());
        if (days.isEmpty()) {
            return 0;
        }
        LocalDate expected = LocalDate.parse(days.getFirst());
        if (expected.isBefore(today.minusDays(1))) {
            return 0;
        }
        int streak = 0;
        for (String day : days) {
            if (!LocalDate.parse(day).equals(expected)) {
                break;
            }
            streak++;
            expected = expected.minusDays(1);
        }
        return streak;
    }

    private List<DailyPlays> last7Days(LocalDate today) {
        LocalDate first = today.minusDays(LAST_DAYS - 1);
        Instant since = first.atStartOfDay(zone).toInstant();

        Map<LocalDate, Long> counts = new HashMap<>();
        for (Object[] row : playRepo.playsPerDaySince(zone.getId(), since)) {
            counts.put(LocalDate.parse((String) row[0]), ((Number) row[1]).longValue());
        }

        List<DailyPlays> result = new ArrayList<>(LAST_DAYS);
        for (LocalDate day = first; !day.isAfter(today); day = day.plusDays(1)) {
            result.add(new DailyPlays(day, counts.getOrDefault(day, 0L)));
        }
        return result;
    }
}
