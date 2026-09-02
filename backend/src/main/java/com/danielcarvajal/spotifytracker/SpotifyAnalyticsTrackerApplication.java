package com.danielcarvajal.spotifytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpotifyAnalyticsTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyAnalyticsTrackerApplication.class, args);
	}

}
