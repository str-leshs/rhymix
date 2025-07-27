package TeamRhymix.Rhymix.domain.playlist.controller;

import TeamRhymix.Rhymix.domain.playlist.entity.Playlist;
import TeamRhymix.Rhymix.domain.playlist.dto.PlaylistDto;
import TeamRhymix.Rhymix.global.exception.ErrorCode;
import TeamRhymix.Rhymix.global.exception.PlaylistException;
import TeamRhymix.Rhymix.domain.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final MongoTemplate mongoTemplate;

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
    public PlaylistDto getPlaylistDetail(@PathVariable String id, @AuthenticationPrincipal UserDetails user) {
        if ("me".equals(id)) {
            if (user == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            String nickname = user.getUsername();
            Playlist latest = playlistService.findLatestMonthlyPlaylistByNickname(nickname);
            if (latest == null) {
                throw new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND);
            }

            return playlistService.getPlaylistWithTracks(latest.getId().toString());
        }

        return playlistService.getPlaylistWithTracks(id);
    }

    /**
     * 태그(기분 또는 날씨)를 기반으로 테마 플레이리스트 생성
     * - 로그인 사용자에 한해 동작
     * - 동일한 테마가 기존에 존재하면 삭제 후 새로 생성
     */
    @PostMapping("/generate/theme")
    public ResponseEntity<PlaylistDto> generateThemePlaylist(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails user
    ) {
        if (user == null) {
            throw new PlaylistException(ErrorCode.UNAUTHORIZED);
        }

        String tag = payload.get("tag");
        String nickname = user.getUsername();
        PlaylistDto dto = playlistService.generateThemePlaylist(nickname, tag);
        return ResponseEntity.ok(dto);
    }

    /**
     * 로그인한 사용자의 테마 플레이스트 조회
     */
    @GetMapping("/theme")
    public ResponseEntity<PlaylistDto> getThemePlaylist(
            @RequestParam String tag,
            @AuthenticationPrincipal UserDetails user
    ) {
        if (user == null) {
            throw new PlaylistException(ErrorCode.UNAUTHORIZED);
        }

        String nickname = user.getUsername();
        PlaylistDto dto = playlistService.getThemePlaylistPreview(nickname, tag);
        return ResponseEntity.ok(dto);
    }



    /**
     * 이웃 사용자의 최신 월별 플레이리스트 조회
     */
    @GetMapping("/monthly")
    public ResponseEntity<PlaylistDto> getLatestMonthlyPlaylistByNickname(@RequestParam String nickname) {
        Playlist latest = playlistService.findLatestMonthlyPlaylistByNickname(nickname);
        if (latest == null) {
            throw new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND);
        }

        return ResponseEntity.ok(playlistService.getPlaylistWithTracks(latest.getId().toString()));
    }


}

