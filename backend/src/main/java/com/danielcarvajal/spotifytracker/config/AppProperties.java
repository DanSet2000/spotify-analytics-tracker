package com.danielcarvajal.spotifytracker.config;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(ZoneId timezone, Security security, Cors cors) {

    public record Security(String username, String password, String jwtSecret, Duration jwtTtl) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}
