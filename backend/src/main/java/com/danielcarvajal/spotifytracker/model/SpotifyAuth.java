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

    public static final int SINGLE_ROW_ID = 1;

    @Id
    private Integer id = SINGLE_ROW_ID;

    @Column(nullable = false)
    private String refreshToken;

    private Instant updatedAt;
}
