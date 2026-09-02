package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.config.SpotifyProperties;
import com.danielcarvajal.spotifytracker.dto.SpotifyTokenResponse;
import com.danielcarvajal.spotifytracker.model.SpotifyAuth;
import com.danielcarvajal.spotifytracker.repository.SpotifyAuthRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SpotifyAuthService {

    private static final String AUTHORIZE_URL = "https://accounts.spotify.com/authorize";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SCOPES =
            "user-read-currently-playing user-read-playback-state user-read-recently-played";

    private final SpotifyProperties props;
    private final SpotifyAuthRepository repo;
    private final RestClient restClient = RestClient.create();

    private String cachedAccessToken;
    private Instant cachedExpiry = Instant.EPOCH;

    public SpotifyAuthService(SpotifyProperties props, SpotifyAuthRepository repo) {
        this.props = props;
        this.repo = repo;
    }

    public String newState() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String buildAuthorizeUrl(String state) {
        return UriComponentsBuilder.fromUriString(AUTHORIZE_URL)
                .queryParam("client_id", props.clientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", props.redirectUri())
                .queryParam("scope", SCOPES)
                .queryParam("state", state)
                .encode()
                .build()
                .toUriString();
    }

    public synchronized void exchangeCode(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", props.redirectUri());

        SpotifyTokenResponse token = requestToken(form);
        saveRefreshToken(token.refreshToken());
        cache(token);
    }

    public synchronized String getAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(cachedExpiry)) {
            return cachedAccessToken;
        }

        SpotifyAuth auth = repo.findById(1)
                .orElseThrow(() -> new IllegalStateException(
                        "Spotify no esta conectado. Visita /api/spotify/login"));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", auth.getRefreshToken());

        SpotifyTokenResponse token = requestToken(form);
        if (token.refreshToken() != null) {
            saveRefreshToken(token.refreshToken());
        }
        cache(token);
        return cachedAccessToken;
    }

    public boolean isConnected() {
        return repo.existsById(1);
    }

    public synchronized void invalidateAccessToken() {
        cachedAccessToken = null;
        cachedExpiry = Instant.EPOCH;
    }

    private SpotifyTokenResponse requestToken(MultiValueMap<String, String> form) {
        return restClient.post()
                .uri(TOKEN_URL)
                .headers(h -> h.setBasicAuth(props.clientId(), props.clientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(SpotifyTokenResponse.class);
    }

    private void saveRefreshToken(String refreshToken) {
        SpotifyAuth auth = repo.findById(1).orElseGet(SpotifyAuth::new);
        auth.setRefreshToken(refreshToken);
        auth.setUpdatedAt(Instant.now());
        repo.save(auth);
    }

    private void cache(SpotifyTokenResponse token) {
        cachedAccessToken = token.accessToken();
        cachedExpiry = Instant.now().plusSeconds(token.expiresIn() - 60);
    }
}
