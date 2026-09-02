package com.danielcarvajal.spotifytracker.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TitleNormalizer {

    private static final String EDITION_WORDS =
            "remaster(?:ed)?|deluxe|expanded|anniversary|edition|bonus|explicit|clean"
            + "|reissue|re-issue|special|collector'?s|mono|stereo";

    private static final Pattern BRACKETED = Pattern.compile(
            "\\s*[\\(\\[]([^\\(\\)\\[\\]]*\\b(?:" + EDITION_WORDS + ")\\b[^\\(\\)\\[\\]]*)[\\)\\]]\\s*$",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern DASHED = Pattern.compile(
            "\\s+-\\s+([^-]*\\b(?:" + EDITION_WORDS + ")\\b[^-]*)$",
            Pattern.CASE_INSENSITIVE);

    public record Result(String canonicalName, String editionLabel) {
    }

    public Result normalize(String title) {
        String name = title;
        List<String> labels = new ArrayList<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            Matcher m = BRACKETED.matcher(name);
            if (m.find()) {
                labels.add(m.group(1).trim());
                name = name.substring(0, m.start());
                changed = true;
                continue;
            }
            m = DASHED.matcher(name);
            if (m.find()) {
                labels.add(m.group(1).trim());
                name = name.substring(0, m.start());
                changed = true;
            }
        }
        name = name.trim().replaceAll("\\s+", " ");
        if (name.isEmpty()) {
            name = title.trim();
        }
        Collections.reverse(labels);
        String label = labels.isEmpty() ? "Original" : String.join(", ", labels);
        return new Result(name, label);
    }
}
