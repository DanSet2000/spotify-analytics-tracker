package com.danielcarvajal.spotifytracker.service;

import com.danielcarvajal.spotifytracker.config.SpotifyProperties;
import com.danielcarvajal.spotifytracker.dto.SpotifyTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SpotifyAccountsClient {

    private static final String AUTHORIZE_URL = "https://accounts.spotify.com/authorize";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SCOPES =
            "user-read-currently-playing user-read-playback-state user-read-recently-played";

    private final RestClient restClient;
    private final SpotifyProperties props;

    public SpotifyAccountsClient(RestClient restClient, SpotifyProperties props) {
        this.restClient = restClient;
        this.props = props;
    }

    public String authorizeUrl(String state) {
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

    public SpotifyTokenResponse exchangeCode(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", props.redirectUri());
        return requestToken(form);
    }

    public SpotifyTokenResponse refresh(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        return requestToken(form);
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
}
