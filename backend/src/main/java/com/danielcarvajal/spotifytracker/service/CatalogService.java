package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.dto.SpotifyAlbumDto;
import com.danielcarvajal.spotifytracker.dto.SpotifyArtistDto;
import com.danielcarvajal.spotifytracker.dto.SpotifyTrackDto;
import com.danielcarvajal.spotifytracker.model.Album;
import com.danielcarvajal.spotifytracker.model.Artist;
import com.danielcarvajal.spotifytracker.model.CanonicalAlbum;
import com.danielcarvajal.spotifytracker.model.CanonicalTrack;
import com.danielcarvajal.spotifytracker.model.Track;
import com.danielcarvajal.spotifytracker.repository.AlbumRepository;
import com.danielcarvajal.spotifytracker.repository.ArtistRepository;
import com.danielcarvajal.spotifytracker.repository.CanonicalAlbumRepository;
import com.danielcarvajal.spotifytracker.repository.CanonicalTrackRepository;
import com.danielcarvajal.spotifytracker.repository.TrackRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final ArtistRepository artistRepo;
    private final AlbumRepository albumRepo;
    private final TrackRepository trackRepo;
    private final CanonicalAlbumRepository canonicalAlbumRepo;
    private final CanonicalTrackRepository canonicalTrackRepo;
    private final TitleNormalizer normalizer;

    public CatalogService(ArtistRepository artistRepo,
                          AlbumRepository albumRepo,
                          TrackRepository trackRepo,
                          CanonicalAlbumRepository canonicalAlbumRepo,
                          CanonicalTrackRepository canonicalTrackRepo,
                          TitleNormalizer normalizer) {
        this.artistRepo = artistRepo;
        this.albumRepo = albumRepo;
        this.trackRepo = trackRepo;
        this.canonicalAlbumRepo = canonicalAlbumRepo;
        this.canonicalTrackRepo = canonicalTrackRepo;
        this.normalizer = normalizer;
    }

    @Transactional
    public Track upsertTrack(SpotifyTrackDto dto) {
        return trackRepo.findById(dto.id()).orElseGet(() -> createTrack(dto));
    }

    private Track createTrack(SpotifyTrackDto dto) {
        Album album = upsertAlbum(dto.album(), dto.artists());

        Track track = new Track();
        track.setId(dto.id());
        track.setName(dto.name());
        track.setDurationMs(dto.durationMs());
        track.setAlbum(album);
        for (SpotifyArtistDto artist : dto.artists()) {
            track.getArtists().add(upsertArtist(artist));
        }
        track.setCanonicalTrack(resolveCanonicalTrack(dto.name(), album.getCanonicalAlbum()));
        return trackRepo.save(track);
    }

    private Album upsertAlbum(SpotifyAlbumDto dto, List<SpotifyArtistDto> trackArtists) {
        return albumRepo.findById(dto.id()).orElseGet(() -> {
            List<SpotifyArtistDto> albumArtists =
                    dto.artists() == null || dto.artists().isEmpty() ? trackArtists : dto.artists();
            Artist primary = upsertArtist(albumArtists.get(0));
            TitleNormalizer.Result normalized = normalizer.normalize(dto.name());

            Album album = new Album();
            album.setId(dto.id());
            album.setName(dto.name());
            album.setEditionLabel(normalized.editionLabel());
            album.setReleaseDate(parseReleaseDate(dto.releaseDate(), dto.releaseDatePrecision()));
            album.setCanonicalAlbum(resolveCanonicalAlbum(normalized.canonicalName(), primary));
            return albumRepo.save(album);
        });
    }

    private Artist upsertArtist(SpotifyArtistDto dto) {
        return artistRepo.findById(dto.id()).orElseGet(() -> {
            Artist artist = new Artist();
            artist.setId(dto.id());
            artist.setName(dto.name());
            return artistRepo.save(artist);
        });
    }

    private CanonicalAlbum resolveCanonicalAlbum(String canonicalName, Artist primary) {
        return canonicalAlbumRepo
                .findByCanonicalNameIgnoreCaseAndPrimaryArtist(canonicalName, primary)
                .orElseGet(() -> {
                    CanonicalAlbum canonical = new CanonicalAlbum();
                    canonical.setCanonicalName(canonicalName);
                    canonical.setPrimaryArtist(primary);
                    return canonicalAlbumRepo.save(canonical);
                });
    }

    private CanonicalTrack resolveCanonicalTrack(String trackName, CanonicalAlbum canonicalAlbum) {
        String canonicalName = normalizer.normalize(trackName).canonicalName();
        return canonicalTrackRepo
                .findByCanonicalNameIgnoreCaseAndCanonicalAlbum(canonicalName, canonicalAlbum)
                .orElseGet(() -> {
                    CanonicalTrack canonical = new CanonicalTrack();
                    canonical.setCanonicalName(canonicalName);
                    canonical.setCanonicalAlbum(canonicalAlbum);
                    return canonicalTrackRepo.save(canonical);
                });
    }

    static LocalDate parseReleaseDate(String value, String precision) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (precision == null ? "day" : precision) {
            case "year" -> LocalDate.of(Integer.parseInt(value), 1, 1);
            case "month" -> YearMonth.parse(value).atDay(1);
            default -> LocalDate.parse(value);
        };
    }
}
