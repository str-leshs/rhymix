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
     * 월별 플레이리스트 생성 (POST 요청)
     */
    @PostMapping("/generate/monthly")
    public ResponseEntity<Playlist> generateMonthlyPlaylist(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam String nickname // 클라이언트에서 로그인 유저 닉네임 전달
    ) {
        Playlist playlist = playlistService.generateMonthlyPlaylist(nickname, year, month);
        return ResponseEntity.ok(playlist);
    }

    /**
     * 플레이리스트 ID로 상세 조회
     */
    @GetMapping("/playlists/{id}")
    public PlaylistWithTracksDto getPlaylistDetail(@PathVariable String id, @AuthenticationPrincipal UserDetails user) {
        if ("me".equals(id)) {
            if (user == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            String nickname = user.getUsername();
            Playlist latest = playlistService.findLatestMonthlyPlaylistByNickname(nickname);

            if (latest == null) {
                throw new RuntimeException("월별 플레이리스트를 생성할 수 없습니다.");
            }

            return playlistService.getPlaylistWithTracks(latest.getId().toString());
        }

        return playlistService.getPlaylistWithTracks(id);
    }



}
