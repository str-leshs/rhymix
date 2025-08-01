package TeamRhymix.Rhymix.domain.user.controller;

import TeamRhymix.Rhymix.domain.post.entity.Post;
import TeamRhymix.Rhymix.domain.track.entity.Track;
import TeamRhymix.Rhymix.domain.post.dto.PostDto;
import TeamRhymix.Rhymix.domain.post.mapper.PostMapper;
import TeamRhymix.Rhymix.domain.post.service.PostService;
import TeamRhymix.Rhymix.domain.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final TrackService trackService;

    @GetMapping("/events")
    public List<Map<String, Object>> getUserPostEvents(@RequestParam String userId) {
        List<Post> posts = postService.getPostsByUserId(userId);

        return posts.stream()
                .filter(post -> post.getTrackId() != null)  // null 필터링 추가
                .map(post -> {
                    Track track = trackService.findByTrackId(post.getTrackId());
                    if (track == null) return null;  // 트랙이 삭제되었을 경우 방어 로직 추가

                    Map<String, Object> event = new HashMap<>();
                    event.put("title", track.getTitle());
                    event.put("start", post.getCreatedAt().toLocalDate().toString());
                    event.put("cover", track.getCoverImage());
                    event.put("trackId", track.getTrackId());
                    return event;
                })
                .filter(Objects::nonNull)  // map 결과가 null인 경우 제거
                .toList();
    }


    @GetMapping("/date")
    public ResponseEntity<PostDto> getPostByDate(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Post post = postService.getPostByDate(userId, date);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postMapper.toDto(post));
    }
}

