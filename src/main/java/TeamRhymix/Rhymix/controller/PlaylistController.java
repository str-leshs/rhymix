package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.Playlist;
import TeamRhymix.Rhymix.dto.PlaylistWithTracksDto;
import TeamRhymix.Rhymix.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * 로그인 사용자의 월별 플레이리스트 생성
     */
    @PostMapping("/generate/monthly")
    public ResponseEntity<Playlist> generateMonthlyPlaylist(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal UserDetails user //여기서 user 추출
    ) {
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String nickname = user.getUsername();
        Playlist playlist = playlistService.generateMonthlyPlaylist(nickname, year, month);
        return ResponseEntity.ok(playlist);
    }

    /**
     * 플레이리스트 ID 또는 최신 월별 플레이리스트 조회
     */
    @GetMapping("/{id}")
    public PlaylistWithTracksDto getPlaylistDetail(@PathVariable String id, @AuthenticationPrincipal UserDetails user) {
        if ("me".equals(id)) {
            if (user == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            String nickname = user.getUsername();
            Playlist latest = playlistService.findLatestMonthlyPlaylistByNickname(nickname);
            if (latest == null) {
                throw new RuntimeException("월별 플레이리스트가 존재하지 않습니다.");
            }

            return playlistService.getPlaylistWithTracks(latest.getId().toString());
        }

        return playlistService.getPlaylistWithTracks(id);
    }
}

