package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.domain.User;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    /**
     * 오늘의 추천곡 저장 API
     * - 클라이언트로부터 받은 PostDto를 Post 엔티티로 변환 후 저장합니다.
     * - 저장된 Post 엔티티를 다시 DTO로 변환해 반환합니다.
     *
     * @param postDto 오늘의 추천곡 정보
     * @return 저장된 추천곡 정보 (PostDto)
     */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        Post saved = postService.savePost(postDto);
        return ResponseEntity.ok(postMapper.toDto(saved));
    }

    @GetMapping("/today")
    public ResponseEntity<PostDto> getTodayPost(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build(); // 로그인하지 않은 경우
        }

        Post post = postService.getTodayPost(user.getNickname());
        return post != null ? ResponseEntity.ok(postMapper.toDto(post)) : ResponseEntity.notFound().build();
    }


    /**
     * [로그인과 통합 후 변경 예정]
     * 특정 사용자 ID로 오늘의 추천곡을 조회하는 API
     * 프론트엔드에서 쿼리 파라미터로 userId를 전달받아 처리합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 오늘의 추천곡 또는 404
     */
//    @GetMapping("/today")
//    public ResponseEntity<PostDto> getTodayPost(@RequestParam String userId) {
//        Post post = postService.getTodayPost(userId);
//        return post != null ? ResponseEntity.ok(postMapper.toDto(post)) : ResponseEntity.notFound().build();
//    }
}
