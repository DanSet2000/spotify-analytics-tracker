package com.danielcarvajal.spotifytracker.service;

import org.springframework.stereotype.Component;

@Component
public class ListeningRule {

    static final long MIN_MS_PLAYED = 90_000;
    static final double SHORT_TRACK_COMPLETION = 0.9;

    public boolean countsAsPlay(int durationMs, long msPlayed) {
        if (durationMs <= MIN_MS_PLAYED) {
            return msPlayed >= durationMs * SHORT_TRACK_COMPLETION;
        }
        return msPlayed > MIN_MS_PLAYED;
    }
}
