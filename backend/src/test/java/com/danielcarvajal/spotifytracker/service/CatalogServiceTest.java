package com.danielcarvajal.spotifytracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CatalogServiceTest {

    @ParameterizedTest(name = "{0} ({1}) -> {2}")
    @CsvSource({
            "1985,       year,  1985-01-01",
            "1985-06,    month, 1985-06-01",
            "2022-03-18, day,   2022-03-18",
            "1985,       ,      1985-01-01",
            "1985-06,    ,      1985-06-01",
            "2022-03-18, ,      2022-03-18",
    })
    void parsesSpotifyReleaseDates(String value, String precision, LocalDate expected) {
        assertThat(CatalogService.parseReleaseDate(value, precision)).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenReleaseDateIsMissing() {
        assertThat(CatalogService.parseReleaseDate(null, "day")).isNull();
        assertThat(CatalogService.parseReleaseDate("  ", "day")).isNull();
    }
}
