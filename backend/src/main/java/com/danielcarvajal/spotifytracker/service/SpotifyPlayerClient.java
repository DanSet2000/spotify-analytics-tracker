package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.dto.CurrentlyPlayingResponse;
import com.danielcarvajal.spotifytracker.dto.RecentlyPlayedResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class SpotifyPlayerClient {

    private static final String CURRENTLY_PLAYING_URL =
            "https://api.spotify.com/v1/me/player/currently-playing";
    private static final String RECENTLY_PLAYED_URL =
            "https://api.spotify.com/v1/me/player/recently-played?limit=50";

    private final RestClient restClient;
    private final SpotifyAuthService authService;

    public SpotifyPlayerClient(RestClient restClient, SpotifyAuthService authService) {
        this.restClient = restClient;
        this.authService = authService;
    }

    public Optional<CurrentlyPlayingResponse> currentlyPlaying() {
        return Optional.ofNullable(get(CURRENTLY_PLAYING_URL, CurrentlyPlayingResponse.class));
    }

    public List<RecentlyPlayedResponse.Item> recentlyPlayed() {
        RecentlyPlayedResponse body = get(RECENTLY_PLAYED_URL, RecentlyPlayedResponse.class);
        return body == null || body.items() == null ? List.of() : body.items();
    }

    private <T> T get(String url, Class<T> type) {
        try {
            return restClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(authService.getAccessToken()))
                    .retrieve()
                    .body(type);
        } catch (HttpClientErrorException.Unauthorized e) {
            authService.invalidateAccessToken();
            throw e;
        }
    }
}
