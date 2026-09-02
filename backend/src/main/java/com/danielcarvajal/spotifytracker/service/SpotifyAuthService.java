package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.dto.SpotifyStatus;
import com.danielcarvajal.spotifytracker.dto.SpotifyTokenResponse;
import com.danielcarvajal.spotifytracker.model.SpotifyAuth;
import com.danielcarvajal.spotifytracker.repository.SpotifyAuthRepository;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class SpotifyAuthService {

    private static final Duration STATE_TTL = Duration.ofMinutes(10);
    private static final long EXPIRY_MARGIN_SECONDS = 60;

    private final SpotifyAccountsClient accounts;
    private final SpotifyAuthRepository repo;

    private String cachedAccessToken;
    private Instant cachedExpiry = Instant.EPOCH;

    private String pendingState;
    private Instant pendingStateExpiry = Instant.EPOCH;

    public SpotifyAuthService(SpotifyAccountsClient accounts, SpotifyAuthRepository repo) {
        this.accounts = accounts;
        this.repo = repo;
    }

    public synchronized String beginAuthorization() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        pendingState = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        pendingStateExpiry = Instant.now().plus(STATE_TTL);
        return accounts.authorizeUrl(pendingState);
    }

    public synchronized boolean consumeState(String state) {
        boolean valid = pendingState != null
                && pendingState.equals(state)
                && Instant.now().isBefore(pendingStateExpiry);
        pendingState = null;
        return valid;
    }

    public synchronized void exchangeCode(String code) {
        SpotifyTokenResponse token = accounts.exchangeCode(code);
        saveRefreshToken(token.refreshToken());
        cache(token);
    }

    public synchronized String getAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(cachedExpiry)) {
            return cachedAccessToken;
        }

        SpotifyAuth auth = repo.findCurrent()
                .orElseThrow(() -> new IllegalStateException(
                        "Spotify no esta conectado. Visita /api/spotify/login"));

        SpotifyTokenResponse token = accounts.refresh(auth.getRefreshToken());
        if (token.refreshToken() != null) {
            saveRefreshToken(token.refreshToken());
        }
        cache(token);
        return cachedAccessToken;
    }

    public synchronized void invalidateAccessToken() {
        cachedAccessToken = null;
        cachedExpiry = Instant.EPOCH;
    }

    public boolean isConnected() {
        return repo.hasCurrent();
    }

    public SpotifyStatus status() {
        return repo.findCurrent()
                .map(auth -> new SpotifyStatus(true, auth.getUpdatedAt()))
                .orElseGet(() -> new SpotifyStatus(false, null));
    }

    private void saveRefreshToken(String refreshToken) {
        SpotifyAuth auth = repo.findCurrent().orElseGet(SpotifyAuth::new);
        auth.setRefreshToken(refreshToken);
        auth.setUpdatedAt(Instant.now());
        repo.save(auth);
    }

    private void cache(SpotifyTokenResponse token) {
        cachedAccessToken = token.accessToken();
        cachedExpiry = Instant.now().plusSeconds(token.expiresIn() - EXPIRY_MARGIN_SECONDS);
    }
}
