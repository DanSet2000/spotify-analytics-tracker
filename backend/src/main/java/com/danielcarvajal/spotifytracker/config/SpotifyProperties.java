package com.danielcarvajal.spotifytracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spotify")
public record SpotifyProperties(String clientId, String clientSecret, String redirectUri) {
}
