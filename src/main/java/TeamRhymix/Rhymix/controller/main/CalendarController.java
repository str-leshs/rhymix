package TeamRhymix.Rhymix.controller.main;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.domain.Track;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.PostService;
import TeamRhymix.Rhymix.service.TrackService;
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

        return posts.stream().map(post -> {
            Map<String, Object> event = new HashMap<>();
            Track track = trackService.findByTrackId(post.getTrackId());  // Track 정보 조회

            event.put("title", track.getTitle());                         //Post → Track에서
            event.put("start", post.getCreatedAt().toLocalDate().toString());
            event.put("cover", track.getCoverImage());
            event.put("trackId", track.getTrackId());
            return event;
        }).toList();
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

