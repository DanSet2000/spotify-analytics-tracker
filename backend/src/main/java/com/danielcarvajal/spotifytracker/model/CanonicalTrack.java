package com.danielcarvajal.spotifytracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"canonical_name", "canonical_album_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CanonicalTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String canonicalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canonical_album_id", nullable = false)
    private CanonicalAlbum canonicalAlbum;
}
