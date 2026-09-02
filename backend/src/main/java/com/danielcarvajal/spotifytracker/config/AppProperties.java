package com.danielcarvajal.spotifytracker.config;

import java.time.ZoneId;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(ZoneId timezone) {
}
