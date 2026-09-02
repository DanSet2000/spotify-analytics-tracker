package com.danielcarvajal.spotifytracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ListeningRuleTest {

    private final ListeningRule rule = new ListeningRule();

    @ParameterizedTest(name = "duracion {0} ms, escuchado {1} ms -> {2}")
    @CsvSource({
            "240000,  90000, false",
            "240000,  90001, true",
            "240000, 240000, true",
            " 75000,  67500, true",
            " 75000,  60000, false",
            " 90000,  81000, true",
            " 90000,  80000, false",
            " 45000,  45000, true",
    })
    void countsAsPlay(int durationMs, long msPlayed, boolean expected) {
        assertThat(rule.countsAsPlay(durationMs, msPlayed)).isEqualTo(expected);
    }
}
