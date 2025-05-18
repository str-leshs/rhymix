package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 사용자 ID를 기반으로 해당 사용자가 등록한 추천곡(Post)을 조회
 */
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final PostService postService;

    /**
     * 특정 사용자의 추천곡(Post)들을 캘린더 이벤트 형식으로 반환합니다.
     * 요청 URL 형식: /api/calendar/events?userId=---
     */

    @GetMapping("/events")
    public List<Map<String, Object>> getUserPostEvents(@RequestParam String userId) {
        List<Post> posts = postService.getPostsByUserId(userId);

        //TODO Map<String, Object> 구조로 반환하면, 프론트에서 자유롭게 커스텀하기
        return posts.stream().map(post -> {
            Map<String, Object> event = new HashMap<>();
            event.put("title", post.getTitle());
            event.put("start", post.getCreatedAt().toLocalDate().toString());
            event.put("cover", post.getCover());
            return event;
        }).toList();
    }

}
