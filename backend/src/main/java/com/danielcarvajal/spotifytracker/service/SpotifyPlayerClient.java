package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.dto.CurrentlyPlayingResponse;
import com.danielcarvajal.spotifytracker.dto.RecentlyPlayedResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SpotifyPlayerClient {

    private static final String CURRENTLY_PLAYING_URL =
            "https://api.spotify.com/v1/me/player/currently-playing";
    private static final String RECENTLY_PLAYED_URL =
            "https://api.spotify.com/v1/me/player/recently-played?limit=50";

    private final SpotifyAuthService authService;
    private final RestClient restClient = RestClient.create();

    public SpotifyPlayerClient(SpotifyAuthService authService) {
        this.authService = authService;
    }

    public Optional<CurrentlyPlayingResponse> currentlyPlaying() {
        CurrentlyPlayingResponse body = restClient.get()
                .uri(CURRENTLY_PLAYING_URL)
                .headers(h -> h.setBearerAuth(authService.getAccessToken()))
                .retrieve()
                .body(CurrentlyPlayingResponse.class);
        return Optional.ofNullable(body);
    }

    public List<RecentlyPlayedResponse.Item> recentlyPlayed() {
        RecentlyPlayedResponse body = restClient.get()
                .uri(RECENTLY_PLAYED_URL)
                .headers(h -> h.setBearerAuth(authService.getAccessToken()))
                .retrieve()
                .body(RecentlyPlayedResponse.class);
        return body == null || body.items() == null ? List.of() : body.items();
    }
}
