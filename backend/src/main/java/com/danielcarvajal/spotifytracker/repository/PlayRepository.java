package com.danielcarvajal.spotifytracker.repository;

import com.danielcarvajal.spotifytracker.dto.AlbumEdition;
import com.danielcarvajal.spotifytracker.dto.AlbumStats;
import com.danielcarvajal.spotifytracker.dto.ArtistStats;
import com.danielcarvajal.spotifytracker.dto.TrackStats;
import com.danielcarvajal.spotifytracker.model.Play;
import com.danielcarvajal.spotifytracker.model.Track;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayRepository extends JpaRepository<Play, UUID> {

    boolean existsByTrackAndEndedAtBetween(Track track, Instant from, Instant to);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.ArtistStats(a.id, a.name, count(p))
            from Play p
            join p.track t
            join t.artists a
            group by a.id, a.name
            order by count(p) desc, a.name
            """)
    List<ArtistStats> topArtists(Limit limit);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.AlbumStats(ca.id, ca.canonicalName, ar.name, count(p))
            from Play p
            join p.track t
            join t.album al
            join al.canonicalAlbum ca
            join ca.primaryArtist ar
            group by ca.id, ca.canonicalName, ar.name
            order by count(p) desc, ca.canonicalName
            """)
    List<AlbumStats> topAlbums(Limit limit);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.TrackStats(ct.id, ct.canonicalName, ca.canonicalName, ar.name, count(p))
            from Play p
            join p.track t
            join t.canonicalTrack ct
            join ct.canonicalAlbum ca
            join ca.primaryArtist ar
            group by ct.id, ct.canonicalName, ca.canonicalName, ar.name
            order by count(p) desc, ct.canonicalName
            """)
    List<TrackStats> topTracks(Limit limit);

    @Query("""
            select count(p)
            from Play p
            join p.track t
            join t.artists a
            where a.id = :artistId
            """)
    long countByArtist(@Param("artistId") String artistId);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.AlbumStats(ca.id, ca.canonicalName, ar.name, count(p))
            from Play p
            join p.track t
            join t.artists a
            join t.album al
            join al.canonicalAlbum ca
            join ca.primaryArtist ar
            where a.id = :artistId
            group by ca.id, ca.canonicalName, ar.name
            order by count(p) desc, ca.canonicalName
            """)
    List<AlbumStats> topAlbumsByArtist(@Param("artistId") String artistId, Limit limit);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.TrackStats(ct.id, ct.canonicalName, ca.canonicalName, ar.name, count(p))
            from Play p
            join p.track t
            join t.artists a
            join t.canonicalTrack ct
            join ct.canonicalAlbum ca
            join ca.primaryArtist ar
            where a.id = :artistId
            group by ct.id, ct.canonicalName, ca.canonicalName, ar.name
            order by count(p) desc, ct.canonicalName
            """)
    List<TrackStats> topTracksByArtist(@Param("artistId") String artistId, Limit limit);

    @Query("""
            select count(p)
            from Play p
            join p.track t
            join t.album al
            where al.canonicalAlbum.id = :canonicalAlbumId
            """)
    long countByCanonicalAlbum(@Param("canonicalAlbumId") UUID canonicalAlbumId);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.TrackStats(ct.id, ct.canonicalName, ca.canonicalName, ar.name, count(p))
            from Play p
            join p.track t
            join t.canonicalTrack ct
            join ct.canonicalAlbum ca
            join ca.primaryArtist ar
            where ca.id = :canonicalAlbumId
            group by ct.id, ct.canonicalName, ca.canonicalName, ar.name
            order by count(p) desc, ct.canonicalName
            """)
    List<TrackStats> topTracksByCanonicalAlbum(@Param("canonicalAlbumId") UUID canonicalAlbumId, Limit limit);

    @Query("""
            select new com.danielcarvajal.spotifytracker.dto.AlbumEdition(al.id, al.name, al.editionLabel, al.releaseDate, count(p))
            from Play p
            join p.track t
            join t.album al
            where al.canonicalAlbum.id = :canonicalAlbumId
            group by al.id, al.name, al.editionLabel, al.releaseDate
            order by count(p) desc, al.releaseDate
            """)
    List<AlbumEdition> editionsOf(@Param("canonicalAlbumId") UUID canonicalAlbumId);

    @Query("select coalesce(sum(p.msPlayed), 0) from Play p")
    long totalMsPlayed();

    @Query(value = """
            select distinct to_char(p.played_at at time zone :tz, 'YYYY-MM-DD') as day
            from play p
            order by day desc
            """, nativeQuery = true)
    List<String> distinctPlayDays(@Param("tz") String tz);

    @Query(value = """
            select to_char(p.played_at at time zone :tz, 'YYYY-MM-DD') as day, count(*) as plays
            from play p
            where p.played_at >= :since
            group by day
            order by day
            """, nativeQuery = true)
    List<Object[]> playsPerDaySince(@Param("tz") String tz, @Param("since") Instant since);
}
