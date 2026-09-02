package com.danielcarvajal.spotifytracker.controller;

import com.danielcarvajal.spotifytracker.dto.SpotifyLoginUrl;
import com.danielcarvajal.spotifytracker.dto.SpotifyStatus;
import com.danielcarvajal.spotifytracker.service.SpotifyAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyAuthController {

    private final SpotifyAuthService authService;

    public SpotifyAuthController(SpotifyAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public SpotifyLoginUrl login() {
        String state = authService.createState();
        return new SpotifyLoginUrl(authService.buildAuthorizeUrl(state));
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {

        if (error != null) {
            return ResponseEntity.badRequest().body("Spotify rechazo la autorizacion: " + error);
        }
        if (code == null || !authService.consumeState(state)) {
            return ResponseEntity.badRequest().body("State invalido o expirado, vuelve a iniciar la conexion");
        }

        authService.exchangeCode(code);
        return ResponseEntity.ok("Spotify conectado. Ya puedes cerrar esta pestana.");
    }

    @GetMapping("/status")
    public SpotifyStatus status() {
        return authService.status();
    }
}
