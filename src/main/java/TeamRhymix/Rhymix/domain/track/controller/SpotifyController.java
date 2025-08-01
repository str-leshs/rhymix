package TeamRhymix.Rhymix.domain.track.controller;

import TeamRhymix.Rhymix.domain.track.dto.SpotifyTrackDto;
import TeamRhymix.Rhymix.domain.auth.service.SpotifyAuthService;
import TeamRhymix.Rhymix.domain.post.service.SpotifySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifySearchService spotifySearchService;

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = spotifyAuthService.getAccessToken();
        return ResponseEntity.ok(token);
    }


    @GetMapping("/search")
    public ResponseEntity<List<SpotifyTrackDto>> searchTracks(@RequestParam String query) {
        List<SpotifyTrackDto> results = spotifySearchService.searchTracks(query);
        return ResponseEntity.ok(results);
    }
}
