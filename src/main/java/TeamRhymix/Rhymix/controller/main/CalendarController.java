package TeamRhymix.Rhymix.controller.main;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * 사용자 ID를 기반으로 해당 사용자가 등록한 추천곡(Post)을 조회
 */
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final PostService postService;
    private final PostMapper postMapper;

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
//    @GetMapping("/api/calendar/events")
//    public List<Map<String, Object>> getUserPostEvents(@RequestParam String nickname) {
//        List<Post> posts = postService.getPostsByUserId(nickname); // nickname을 userId로 사용 중
//
//        return posts.stream().map(post -> {
//            Map<String, Object> event = new HashMap<>();
//            event.put("title", post.getTitle());
//            event.put("start", post.getCreatedAt().toLocalDate().toString());
//            event.put("cover", post.getCover());
//            return event;
//        }).toList();
//    }

    //TODO 앨범 클릭 ->  해당 날짜의 추천곡 상세 정보를 모달창에 표시
    /**
     * 특정 날짜의 추천곡(Post)을 조회
     * - 프론트엔드에서 캘린더 셀(앨범 커버) 클릭 시 호출
     * - 해당 날짜에 해당 사용자가 등록한 추천곡이 없으면 404 반환
     * 요청 예시:
     * - GET /api/calendar/date?userId=lion01&date=2025-05-17
     * @return 추천곡이 있으면 PostDto, 없으면 404 Not Found
     */
    @GetMapping("/date")
    public ResponseEntity<PostDto> getPostByDate(
            @RequestParam String userId,
            // HTTP 요청 파라미터로 전달된 문자열을 LocalDate로 변환하기 위한 표준 방식
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Post post = postService.getPostByDate(userId, date);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postMapper.toDto(post));
    }
//    @GetMapping("/api/calendar/date")
//    public ResponseEntity<PostDto> getPostByDate(
//            @RequestParam String nickname,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
//    ) {
//        Post post = postService.getPostByDate(nickname, date);
//        if (post == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(postMapper.toDto(post));
//    }


}
