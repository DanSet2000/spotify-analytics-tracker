package com.danielcarvajal.spotifytracker.controller;

import com.danielcarvajal.spotifytracker.service.SpotifyAuthService;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyAuthController {

    private static final String STATE_KEY = "spotify_oauth_state";

    private final SpotifyAuthService authService;

    public SpotifyAuthController(SpotifyAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(HttpSession session) {
        String state = authService.newState();
        session.setAttribute(STATE_KEY, state);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(authService.buildAuthorizeUrl(state)))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpSession session) {

        if (error != null) {
            return ResponseEntity.badRequest().body("Spotify rechazo la autorizacion: " + error);
        }

        String expected = (String) session.getAttribute(STATE_KEY);
        session.removeAttribute(STATE_KEY);
        if (expected == null || !expected.equals(state)) {
            return ResponseEntity.badRequest().body("State invalido, vuelve a /api/spotify/login");
        }

        authService.exchangeCode(code);
        return ResponseEntity.ok("Spotify conectado. Ya puedes cerrar esta pestana.");
    }
}
