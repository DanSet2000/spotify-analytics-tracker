package com.danielcarvajal.spotifytracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SpotifyAuth {

    @Id
    private Integer id = 1;

    @Column(nullable = false)
    private String refreshToken;

    private Instant updatedAt;
}
