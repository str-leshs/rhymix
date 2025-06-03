package TeamRhymix.Rhymix.controller;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.dto.CommentDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.service.PostService;
import TeamRhymix.Rhymix.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    /** ✅ 오늘의 추천곡 저장 */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        System.out.println("✅ POST 저장 요청 수신");
        System.out.println("제목: " + postDto.getTitle());
        System.out.println("내용(comment): " + postDto.getComment());

        Post saved = postService.savePost(postDto);
        return ResponseEntity.ok(postMapper.toDto(saved));
    }

    /** ✅ 오늘의 추천곡 가져오기 */
    @GetMapping("/today")
    public ResponseEntity<PostDto> getTodayPost(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        System.out.println("✅ [/api/posts/today] 요청 받음");

        if (userDetails == null) {
            System.out.println("❌ 로그인 정보 없음");
            return ResponseEntity.status(401).build();
        }

        String userId = userDetails.getUsername();
        System.out.println("🔍 요청한 사용자 ID: " + userId);

        Post post = postService.getTodayPost(userId);

        if (post != null) {
            System.out.println("✅ 오늘의 추천곡 조회 성공: " + post.getTitle());
            return ResponseEntity.ok(postMapper.toDto(post));
        } else {
            System.out.println("📭 오늘의 추천곡 없음 for 사용자: " + userId);
            return ResponseEntity.notFound().build();
        }
    }

    /** ✅ 월별 추천곡 가져오기 */
    @GetMapping("/monthly")
    public ResponseEntity<List<PostDto>> getMonthlyPosts(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam int year,
            @RequestParam int month) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String userId = userDetails.getUsername();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        List<Post> posts = postService.findPostsByUserAndMonth(userId, startDate, endDate);
        List<PostDto> dtos = posts.stream().map(postMapper::toDto).toList();

        return ResponseEntity.ok(dtos);
    }

    /** ✅ 오늘의 곡 댓글 가져오기 */
    @GetMapping("/today/comments")
    public ResponseEntity<List<CommentDto>> getTodayComments(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String userId = userDetails.getUsername();
        List<CommentDto> comments = postService.getCommentsForTodayPost(userId);
        return ResponseEntity.ok(comments);
    }

    /** ✅ 오늘의 곡 댓글 작성 */
    @PostMapping("/today/comments")
    public ResponseEntity<?> addCommentToTodayPost(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestBody CommentDto commentDto) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String userId = userDetails.getUsername();
        postService.addCommentToTodayPost(userId, commentDto.getText());
        return ResponseEntity.ok().build();
    }

    /** ✅ 사용자 최신 글 조회 */
    @GetMapping("/my-latest")
    public ResponseEntity<PostDto> getMyLatestPost(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String userId = userDetails.getUsername();
        Post latestPost = postService.getLatestPost(userId);

        if (latestPost != null) {
            return ResponseEntity.ok(postMapper.toDto(latestPost));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
