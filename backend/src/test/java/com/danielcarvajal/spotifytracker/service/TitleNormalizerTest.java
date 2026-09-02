package com.danielcarvajal.spotifytracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TitleNormalizerTest {

    private final TitleNormalizer normalizer = new TitleNormalizer();

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', textBlock = """
            Thriller (2003 Remaster)                  | Thriller              | 2003 Remaster
            Nevermind (Deluxe Edition)                | Nevermind             | Deluxe Edition
            Billie Jean - 2012 Remaster               | Billie Jean           | 2012 Remaster
            The God That Failed - Remastered 2021     | The God That Failed   | Remastered 2021
            Hallowed Be Thy Name - 2015 Remaster      | Hallowed Be Thy Name  | 2015 Remaster
            Rust in Peace (Deluxe) [Remastered]       | Rust in Peace         | Deluxe, Remastered
            Trust - Remastered 2004 / Remixed         | Trust                 | Remastered 2004 / Remixed
            """)
    void stripsEditionMarkers(String title, String canonical, String label) {
        TitleNormalizer.Result result = normalizer.normalize(title);

        assertThat(result.canonicalName()).isEqualTo(canonical);
        assertThat(result.editionLabel()).isEqualTo(label);
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', textBlock = """
            Thriller
            New Gold (feat. Tame Impala and Bootie Brown)
            Summer Romance (Anti-Gravity Love Song)
            LoveStoned / I Think She Knows (Interlude)
            Tornado Of Souls - 2004 Remix
            This Place Hotel (a.k.a. Heartbreak Hotel)
            """)
    void keepsTitlesThatAreNotEditions(String title) {
        TitleNormalizer.Result result = normalizer.normalize(title);

        assertThat(result.canonicalName()).isEqualTo(title);
        assertThat(result.editionLabel()).isEqualTo("Original");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            thriller (REMASTERED)      | thriller
            Thriller  (2003 Remaster)  | Thriller
            """)
    void isCaseInsensitiveAndTrimsWhitespace(String title, String canonical) {
        assertThat(normalizer.normalize(title).canonicalName()).isEqualTo(canonical);
    }
}
