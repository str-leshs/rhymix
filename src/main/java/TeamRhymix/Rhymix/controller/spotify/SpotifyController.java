package TeamRhymix.Rhymix.controller.spotify;

import TeamRhymix.Rhymix.service.SpotifyAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyAuthService spotifyAuthService;

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = spotifyAuthService.getAccessToken();
        return ResponseEntity.ok(token);
    }
}
